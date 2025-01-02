<!-- IconSelector.vue -->
<template>
  <div>
    <a-button @click="showIcons = true">选择图标</a-button>
    <a-modal
      width="60%"
      :visible="showIcons"
      title="选择图标"
      @ok="handleOk"
      @cancel="handleCancel"
    >
      <div class="icon-list">
        <div
          v-for="(icon, index) in iconList"
          :key="index"
          class="icon-item"
          @click="selectIcon(icon)"
        >
          <component :is="icon" />
          <!--          <i :class="icon.class"></i>-->
          <!--          <span>{{ icon.name }}</span>-->
        </div>
        <div v-if="selectedIcon">
          <h3>已选择的图标:</h3>
          <component :is="selectedIcon" />
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import * as Icons from "@arco-design/web-vue/es/icon";
import { ref } from "vue";
import { defineEmits } from "vue";

const showIcons = ref(false);
const selectedIcon = ref();
const iconList = Object.values(Icons);

const emit = defineEmits(["icon-selected"]);
const handleOk = () => {
  showIcons.value = !showIcons.value;
};

const selectIcon = (icon: any) => {
  selectedIcon.value = icon;
  emit("icon-selected", icon.name);
};

const handleCancel = () => {
  showIcons.value = !showIcons.value;
};
</script>

<style scoped>
.icon-list {
  display: flex;
  flex-wrap: wrap;
}

.icon-item {
  margin: 10px;
  cursor: pointer;
  border: 1px solid transparent;
  padding: 5px;
  transition: border-color 0.3s;
  width: 20px;
  height: 20px;
}

.icon-item:hover {
  border-color: #ccc;
}
</style>
