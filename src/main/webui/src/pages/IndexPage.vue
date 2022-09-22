<template>
  <q-page class="q-pa-md row items-start q-gutter-md">
    <transition v-for="tweet in tweets"
                :key="tweet.id" appear
                enter-active-class="animated zoomInUp"
                leave-active-class="animated zoomOutDown"
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
  </q-page>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useQuasar } from 'quasar';

import EventBus from '@vertx/eventbus-bridge-client.js';

const MAX_CACHED_TWEETS = 5;

const $q = useQuasar();

interface Tweet {
  content: string;
  timestamp: bigint;
  handle: string;
  url: string
}

const tweets = ref<Tweet[]>([])

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
  eb.registerHandler('com.redhat.consulting.tweet', (error: Error, message: any) => {
    if (error) {
      $q.notify({ message: 'Error receiving message from eventbus bridge', type: 'warning' });
    } else {
      if (tweets.value.length >= MAX_CACHED_TWEETS) {
        tweets.value.pop();
      }
      tweets.value.splice(0, 0, JSON.parse(message.body));
    }
  })
}

eb.enableReconnect(true);
</script>
<style lang="sass">
.q-card
  width: 90%
  margin-left: auto
  margin-right: auto
  margin-top: 0.25rem
  margin-bottom: 0.25rem

.handle
  background-color: $accent
  color: white
  font-weight: 800

.birdIcon
  width: 1.25rem
  height: 1.25rem
  margin: auto
</style>
