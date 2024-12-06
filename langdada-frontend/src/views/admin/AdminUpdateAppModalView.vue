<template>
  <div id="adminUpdateAppModal">
    <a-modal
      :visible="isModalVisible"
      title="修改应用信息"
      @cancel="handleCancel"
      @before-ok="handleBeforeOk"
    >
      <a-form :model="app">
        <a-form-item field="appName" label="应用名" disabled>
          <a-input v-model="app.appName" />
        </a-form-item>
        <a-form-item field="appDesc" label="应用描述">
          <a-input v-model="app.appDesc" />
        </a-form-item>
        <a-form-item field="appIcon" label="应用图标">
          <PictureUpload biz="appIcon" :value="app.appIcon" />
        </a-form-item>
        <a-form-item field="appType" label="应用类型">
          <a-select :style="{width:'320px'}" v-model="app.appType" placeholder="Please select ...">
            <a-option :value="0">得分类</a-option>
            <a-option :value="1">测评类</a-option>
          </a-select>
        </a-form-item>
        <a-form-item field="scoringStrategy" label="评分策略">
          <a-select :style="{width:'320px'}" v-model="app.scoringStrategy" placeholder="Please select ...">
            <a-option :value="0">自定义</a-option>
            <a-option :value="1">AI</a-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>
<script setup lang="ts">
import API from "@/api/typings";
import { withDefaults, defineProps, ref, watch } from "vue";
import { defineEmits } from "vue";
import PictureUpload from "@/components/PictureUpload.vue";

interface Props {
  isModalVisible: boolean;
  appData: API.AppVO;
}

const props = withDefaults(defineProps<Props>(), {
  isModalVisible: false,
});

const app = ref<API.AppVO>({ ...props.appData });
watch(
  () => props.appData,
  (newVal) => {
    app.value = { ...newVal };
  }
);
const emit = defineEmits(["cancelModal", "handleUpdateApp"]);
const handleCancel = () => {
  emit("cancelModal", false);
};

const handleBeforeOk = () => {
  emit("handleUpdateApp", app.value);
};
</script>

<style scoped></style>
