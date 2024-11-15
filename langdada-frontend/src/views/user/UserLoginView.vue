<template>
  <div id="userLogin">
    <a-space size="large" :style="{ width: '600px' }">
      <a-form
        class="loginForm"
        :model="loginForm"
        :style="{ width: '600px' }"
        @submit="handleSubmit"
        layout="vertical"
      >
        <a-form-item
          field="账号"
          required
          rules="form.userAccount!=''"
          label="账号"
        >
          <a-input v-model="loginForm.userAccount" placeholder="请输入用户名" />
        </a-form-item>
        <a-form-item field="密码" label="密码" required>
          <a-input-password
            v-model="loginForm.userPassword"
            placeholder="请输入密码"
          />
        </a-form-item>
        <!--        <a-form-item field="isRead">-->
        <!--          <a-checkbox v-model="form.isRead"> I have read the manual</a-checkbox>-->
        <!--        </a-form-item>-->
        <a-form-item>
          <a-button html-type="submit" class="loginBt" type="primary"
            >登录
          </a-button>
          <a-button
            html-type="register"
            @click="registerUser"
            class="registerBt"
            type="primary"
            >注册
          </a-button>
        </a-form-item>
      </a-form>
    </a-space>
  </div>
</template>
<script setup lang="ts">
import { reactive } from "vue";
import { userLoginUsingPost } from "@/api/userController";
import { useLoginUserStore } from "@/store/userStore";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";

const router = useRouter();
const loginForm = reactive({
  userAccount: "",
  userPassword: "",
  isRead: false,
});
// 登录用户信息store
const loginUserStore = useLoginUserStore();

const handleSubmit = async () => {
  await userLoginUsingPost(loginForm).then((res) => {
    if (res.data.code === 0 && res.data.data) {
      loginUserStore.setLoginUser(res.data.data);
      message.success("登录成功");
      // window.location.href = "/";
      router.push({
        path: "/",
      });
    } else {
      message.error("登录失败" + res.data.message);
    }
  });
};

const registerUser = () => {
  router.push({
    path: "/user/register",
  });
};
</script>
<style scoped>
#userLogin {
  align-content: center;
  background: linear-gradient(to right, #fefefe, #fff);
}

.loginForm {
  margin: 16px auto;
  background: linear-gradient(to right, #fefefe, #fff);
}

.loginBt {
  margin: 0 auto;
}
.registerBt {
  margin: 0 auto;
}
</style>
