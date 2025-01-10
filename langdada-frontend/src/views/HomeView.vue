<template>
  <div class="home">
    <a-form layout="inline" @submit="doSearch" v-model="fromSearchParams">
      <a-form-item label="应用名称">
        <a-input v-model="fromSearchParams.appName" />
      </a-form-item>
      <a-form-item label="应用类型">
        <a-select v-model="fromSearchParams.appType" placeholder="请选择">
          <a-option value="0">得分类</a-option>
          <a-option value="1">测评类</a-option>
        </a-select>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="doReset">重置</a-button>
      </a-form-item>
    </a-form>
    <a-list
      class="list-demo-action-layout"
      :grid-props="{ gutter: [390, 20], sm: 24, md: 12, lg: 8, xl: 6 }"
      :bordered="false"
      :data="appList"
      :pagination-props="{
        pageSize: searchParams.pageSize,
        current: searchParams.current,
        total: total,
      }"
    >
      <template #item="{ item }">
        <app-card :app="item" />
      </template>
    </a-list>
  </div>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import API from "@/api/typings";
import AppVO = API.AppVO;
import { listAppVoByPageUsingPost } from "@/api/appController";
import AppCard from "@/components/AppCard.vue";

// 数据总条数
const total = ref(0);

const fromSearchParams = ref<API.AppQueryRequest>({});
/**
 * 初始化搜索分页条件
 */
const initSearchParams = ref({
  pageSize: 10,
  current: 1,
});
const searchParams = ref<API.AppQueryRequest>({
  ...initSearchParams.value,
});
const appList = ref<AppVO[]>([]);

const loadAppList = async () => {
  const params = {
    ...searchParams.value,
    ...fromSearchParams.value,
  };
  params.reviewStatus = 1;
  await listAppVoByPageUsingPost(params).then((res) => {
    if (res.data.code === 0) {
      appList.value = res.data.data?.records ?? [];
      total.value = Number(res.data.data?.total) ?? 0;
    }
  });
};

// 搜索
const doSearch = () => {
  searchParams.value = {
    ...searchParams.value,
    ...fromSearchParams.value,
  };
  loadAppList();
};

// 重置
const doReset = () => {
  searchParams.value = {
    ...initSearchParams.value,
  };
  fromSearchParams.value = {};
  loadAppList();
};

watchEffect(() => {
  loadAppList();
});
</script>
<style scoped>
.list-demo-action-layout .image-area {
  width: 183px;
  height: 119px;
  overflow: hidden;
  border-radius: 2px;
}
.list-demo-action-layout .list-demo-item {
  padding: 20px 0;
  border-bottom: 1px solid var(--color-fill-3);
}
</style>
