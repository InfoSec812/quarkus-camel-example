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

const eb = new EventBus(`${loc.protocol}//${loc.host}:${loc.port}/eventbus/`)

eb.onopen = () => {
  eb.registerHandler('tweet', (error: Error, message: unknown) => {
    if (error) {
      $q.notify({ message: 'Error receiving message from eventbus bridge', type: 'warning' });
    } else {
      tweets.value.push(message.body);
    }
  })
}

</script>
