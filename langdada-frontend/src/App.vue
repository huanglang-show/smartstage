<template>
  <template v-if="route.path.startsWith('/user')">
    <UserLayout />
  </template>
  <template v-else>
    <BasicLayout />
  </template>
</template>

<style>
#app {
}

nav {
  padding: 30px;
}
</style>
<script setup lang="ts">
import BasicLayout from "@/layouts/BasicLayout.vue";
import { useRoute, useRouter } from "vue-router";
import UserLayout from "@/layouts/UserLayout.vue";

const router = useRouter();
const route = useRoute();
</script>
