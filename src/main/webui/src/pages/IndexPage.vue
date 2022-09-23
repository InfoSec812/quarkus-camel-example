<template>
  <q-page class="q-pa-md column items-start q-gutter-md">
    <div class="row">
      <transition v-for="tweet in tweets"
                  :key="tweet.id" appear
                  enter-active-class="animated zoomInLeft"
                  leave-active-class="animated zoomOutRight"
                  duration="800">
        <q-card>
          <q-card-section class="handle">
            <a :href="tweet.url">
              <img src="/icons/twitter.svg" target="_blank" class="birdIcon">
            </a>
            {{ tweet.handle }}
          </q-card-section>
          <q-card-section><span v-html="tweet.content"></span></q-card-section>
        </q-card>
      </transition>
    </div>
    <div class="row" style="width: 100%; height: 100%;">
      <Line :chart-data="chartData" :chart-options="chartOptions" :styles="chartStyles"
            chart-id="volume" :width="1400" :height="400" dataset-id-key="label" />
    </div>
  </q-page>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useQuasar, date } from 'quasar';
import { Line } from 'vue-chartjs'
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  LineElement,
  LinearScale,
  PointElement,
  CategoryScale
} from 'chart.js'

import EventBus from '@vertx/eventbus-bridge-client.js';

ChartJS.register(
  Title,
  Tooltip,
  Legend,
  LineElement,
  LinearScale,
  PointElement,
  CategoryScale
)

const MAX_CACHED_TWEETS = 10;

const $q = useQuasar();

const chartStyles = ref({
  height: '100%',
  width: '100%'
})

interface Tweet {
  content: string;
  timestamp: bigint;
  handle: string;
  url: string
}

const tweets = ref<Tweet[]>([]);

const chartData = ref({
  labels: [] as string[],
  datasets: [{ data: [] as number[], label: 'Tweets per second', backgroundColor: '#ee0000' }]
});

const chartOptions = ref({
  responsive: true
});

const loc = window.location

const ebOptions = {
  vertxbus_reconnect_attempts_max: Infinity, // Max reconnect attempts
  vertxbus_reconnect_delay_min: 1000, // Initial delay (in ms) before first reconnect attempt
  vertxbus_reconnect_delay_max: 5000, // Max delay (in ms) between reconnect attempts
  vertxbus_reconnect_exponent: 2, // Exponential backoff factor
  vertxbus_randomization_factor: 0.5 // Randomization factor between 0 and 1
};

const eb = new EventBus(`${loc.origin}/eventbus/`, ebOptions);

eb.onopen = () => {
  eb.registerHandler('com.redhat.consulting.tweet', (error: Error, message: never) => {
    if (error) {
      $q.notify({ message: 'Error receiving message from eventbus bridge', type: 'warning' });
    } else {
      while (tweets.value.length >= MAX_CACHED_TWEETS) {
        tweets.value.pop();
      }
      tweets.value.splice(0, 0, JSON.parse(message.body));
    }
  })
  eb.registerHandler('com.redhat.consulting.aggregate', (error: Error, message: never) => {
    if (error) {
      $q.notify({ message: 'Error receiving message from eventbus bridge', type: 'warning' });
    } else {
      while (chartData.value.labels.length > MAX_CACHED_TWEETS) {
        chartData.value.labels.splice(0, 1);
        chartData.value.datasets[0].data.splice(0, 1);
      }
      const dataPoint = JSON.parse(message.body);

      const time = date.formatDate(new Date(dataPoint.timestamp), 'HH:mm:ss');
      chartData.value.labels.push(time);
      chartData.value.datasets[0].data.push(dataPoint.count / 10);
    }
  });
}

eb.enableReconnect(true);
</script>
<style lang="sass">
.q-card
  width: 10.55rem
  font-size: 0.5rem
  margin-left: auto
  margin-right: auto
  margin-top: 0.25rem
  margin-bottom: 0.25rem

.handle
  background-color: $accent
  color: white
  font-weight: 800
  font-size: 0.8rem

.birdIcon
  width: 1.25rem
  height: 1.25rem
  margin: auto
</style>
