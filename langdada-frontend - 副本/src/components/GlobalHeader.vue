<template>
  <div id globalHeader>
    <a-menu
      mode="horizontal"
      :default-selected-keys="['1']"
      @menu-item-click="doMenuClick"
    >
      <a-menu-item
        key="0"
        :style="{ padding: 0, marginRight: '38px' }"
        disabled
      >
        <div class="titleBar">
          <img class="logo" src="../../public/logo.jpg" alt="logo" />
          <div class="title">答题平台</div>
        </div>
      </a-menu-item>
      <a-menu-item :key="item.path" v-for="item in showRouters">
        {{ item.name }}
      </a-menu-item>
    </a-menu>
  </div>
</template>

<script setup lang="ts">
import routers from "@/router/routers";
import { computed } from "vue";
import { useRouter } from "vue-router";

const showRouters = computed(() => {
  return routers.filter((item) => {
    if (item.meta && !item.meta.showInHeader) {
      return false;
    }
    return true;
  });
});

const router = useRouter();

const doMenuClick = (key: string) => {
  router.push(key);
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
#globalHeader {
}

.titleBar {
  display: flex;
  align-items: center;
}

.logo {
  width: 40px;
  height: 40px;
  margin-right: 10px;
}

.title {
  font-size: 20px;
  font-weight: bold;
  color: black;
}
</style>
