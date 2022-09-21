<template>
  <q-page class="row items-center justify-evenly">
  </q-page>
</template>

<script setup lang="ts">
import EventBus from '@vertx/eventbus-bridge-client.js'
import { ref } from 'vue'
import { useQuasar } from "quasar";

const $q = useQuasar();

interface Tweet {
  text: string;
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

const eb = new EventBus(`${loc.protocol}//${loc.host}:${loc.port}/eventbus/`, ebOptions);

eb.onopen = () => {
  eb.registerHandler('com.redhat.consulting.tweet', (error: Error, message: unknown) => {
    if (error) {
      $q.notify({ message: 'Error receiving message from eventbus bridge', type: 'warning' });
    } else {
      tweets.value.push(message.body);
    }
  })
}

eb.enableReconnect(true);

</script>
