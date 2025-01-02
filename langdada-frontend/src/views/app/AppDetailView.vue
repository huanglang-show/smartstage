<template>
  <div id="appDetailPage">
    <a-card>
      <a-row>
        <a-col flex="auto" style="margin-bottom: 16px" class="content-wrapper">
          <h2>{{ appData.appName }}</h2>
          <p>{{ appData.appDesc }}</p>
          <p>应用类型：{{ APP_TYPE_MAP[appData.appType] }}</p>
          <p>
            <a-space>
              作者：
              <div :style="{ display: 'flex', alignItems: 'center' }">
                <a-avatar :size="24" :style="{ marginRight: '8px' }"
                  ><img width="1024" src="../../../public/labi.jpeg" alt="logo"
                /></a-avatar>
                <a-typography-text
                  >{{ appData.userName ?? "无名" }}
                </a-typography-text>
              </div>
            </a-space>
          </p>
          <p>
            创建时间：{{
              dayjs(appData.createTime).format("YYYY-MM-DD HH:mm:ss")
            }}
          </p>
          <a-space size="medium">
            <a-button type="primary" @click="doAnswer">开始答题</a-button>
            <a-button @click="doShare">分享应用</a-button>
            <a-button v-if="isMy" @click="doApp">设置应用</a-button>
            <a-button v-if="isMy" @click="doQuestion">设置题目</a-button>
            <a-button v-if="isMy" @click="doResult">设置评分</a-button>
          </a-space>
        </a-col>
        <a-col flex="320px">
          <img width="100%" src="../../../public/labi.jpeg" alt="logo" />
        </a-col>
      </a-row>
    </a-card>
    <ShareModal
      :link="shareLink"
      title="应用分享"
      ref="shareModalRef"
    ></ShareModal>
  </div>
</template>

<script setup lang="ts">
import { withDefaults, defineProps, ref, computed, watchEffect } from "vue";
import API from "@/api/typings";
import { APP_TYPE_MAP } from "@/constant/app";
import dayjs from "dayjs";
import { useLoginUserStore } from "@/store/userStore";
import { getAppVoByIdUsingGet } from "@/api/appController";
import message from "@arco-design/web-vue/es/message";
import ShareModal from "@/components/ShareModal.vue";
import { useRouter } from "vue-router";

interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "";
  },
});

const appData = ref<API.AppVO>({});

const shareModalRef = ref();

const shareLink = `${window.location.protocol}//${window.location.host}/app/detail/${props.id}`;

const router = useRouter();
/**
 * 答题
 */
const doAnswer = () => {
  if (props.id) {
    router.push(`/answer/do/${props.id}`);
  }
};
/**
 * 分享
 */
const doShare = () => {
  if (shareModalRef.value) {
    shareModalRef.value.openModal();
  }
};

const doApp = () => {
  if (props.id) {
    router.push(`/add/app/${props.id}`);
  }
};
const doQuestion = () => {
  if (props.id) {
    router.push(`/add/question/${props.id}`);
  }
};
const doResult = () => {
  if (props.id) {
    router.push(`/add/scoring_result/${props.id}`);
  }
};

const loginUserStore = useLoginUserStore();
let logUserId = loginUserStore.loginUser.id;
// 校验当前应用是否是登录人创建
const isMy = computed(() => {
  return logUserId && logUserId === appData.value.userId;
});

// 根据id查询应用
const loadData = async () => {
  if (!props.id) {
    return;
  }
  await getAppVoByIdUsingGet({
    id: props.id as any,
  }).then((res) => {
    if (res.data.code === 0) {
      appData.value = res.data?.data ?? {};
    } else {
      message.error("获取详情失败，" + res.data.message);
    }
  });
};

watchEffect(() => {
  loadData();
});
</script>
<style scoped>
#appDetailPage .content-wrapper > * {
  margin-bottom: 28px;
}
</style>
