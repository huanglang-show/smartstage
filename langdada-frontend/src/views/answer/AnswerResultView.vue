<template>
  <div id="answerResult">
    <a-card hoverable>
      <a-row>
        <a-col flex="auto" class="content-wrapper">
          <h2>{{ data.resultName }}</h2>
          <p class="result-desc">结果描述：{{ data.resultDesc }}</p>
          <p v-if="data.appType === 0">结果得分：{{ data.resultScore }}</p>
          <p class="result-answer">我的答案：{{ data.choices }}</p>
          <p>应用名：{{ app.appName }}</p>
          <p>应用类型：{{ APP_TYPE_MAP[data.appType] }}</p>
          <p>评分策略：{{ APP_SCORING_STRATEGY_MAP[data.scoringStrategy] }}</p>
          <p>
            <a-space>
              答题人：
              <div>
                <a-avatar :size="24" :style="{ marginRight: '8px' }"
                  ><img width="1024" src="../../../public/logo.jpg" alt="logo"
                /></a-avatar>
                <a-typography-text
                  >{{ data.user?.userName ?? "无名" }}
                </a-typography-text>
              </div>
            </a-space>
          </p>
          <p>
            答题时间：{{ dayjs(data.createTime).format("YYYY-MM-DD HH:MM:ss") }}
          </p>
          <a-space size="medium">
            <a-button type="primary" :href="`/answer/do/${data.appId}`"
              >去答题
            </a-button>
          </a-space>
        </a-col>
        <a-col flex="320px" class="image-col">
          <img width="100%" src="../../../public/logo2.jpg" alt="logo" />
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>
<script setup lang="ts">
import { useRouter } from "vue-router";
import { ref, watchEffect, withDefaults, defineProps } from "vue";
import API from "@/api/typings";
import { getUserAnswerVoByIdUsingGet } from "@/api/userAnswerController";
import message from "@arco-design/web-vue/es/message";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "../../constant/app";
import dayjs from "dayjs";
import { getAppVoByIdUsingGet } from "@/api/appController";

interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return "";
  },
});

const router = useRouter();

// 答题结果
const data = ref<API.UserAnswerVO>({});

// 应用
const app = ref<API.AppVO>({});
const loadData = async () => {
  if (!props.id) {
    return;
  }
  const res = await getUserAnswerVoByIdUsingGet({
    id: props.id as any,
  });
  if (res.data.code === 0) {
    data.value = res.data.data as any;
    await getApp();
  } else {
    message.error("获取答题结果失败" + res.data.message);
  }
};

const getApp = async () => {
  const res = await getAppVoByIdUsingGet({
    id: data.value.appId as any,
  });
  if (res.data.code === 0) {
    app.value = res.data.data as any;
  } else {
    message.error("获取应用信息失败" + res.data.message);
  }
};

watchEffect(() => {
  loadData();
});
</script>

<style scoped>
#answerResultPage .content-wrapper > * {
  margin-bottom: 24px;
  flex: 1;
  margin-right: 20px;
}

.result-desc {
  word-wrap: break-word;
  width: 700px;
}

.result-answer {
  word-wrap: break-word;
  width: 700px;
}

.image-col {
  flex: 0 0 320px;
}
</style>
