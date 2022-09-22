<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <img src="/images/RedHatFedora.png" class="logo" />
        <q-toolbar-title>
          Camel Quarkus Example
        </q-toolbar-title>
      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
</script>
<style lang="sass">
.q-toolbar
  background-color: black

.logo
  width: 3rem
  height: 3rem
</style>
