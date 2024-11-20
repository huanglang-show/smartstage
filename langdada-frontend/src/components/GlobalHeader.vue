<template>
  <a-row id="globalHeader" align="center">
    <a-col flex="auto">
      <a-menu
        mode="horizontal"
        :default-selected-keys="selectKey"
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
    </a-col>
    <a-col flex="100px">
      <a-dropdown-button>
        <template v-if="loginUserStore.loginUser.id">
          {{ loginUserStore.loginUser.userName ?? "无名用户" }}
        </template>
        <template v-else>
          <a-button type="primary" href="/user/login">登录</a-button>
        </template>
        <template #content>
          <a-doption @click="updateUser"> 个人中心</a-doption>
          <a-doption @click="logOut"> logOut</a-doption>
        </template>
      </a-dropdown-button>
    </a-col>
  </a-row>
</template>

<script setup lang="ts">
import routers from "@/router/routers";
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import { useLoginUserStore } from "@/store/userStore";
import checkAccess from "@/access/checkAccess";

const router = useRouter();
// 获取登录用户信息
const loginUserStore = useLoginUserStore();
const loginUser = loginUserStore.loginUser;

// 过滤掉不需要在头部显示的路由
const showRouters = computed(() => {
  return routers.filter((item) => {
    if (item.meta && item.meta.hideInMenu) {
      return false;
    }
    if (!checkAccess(loginUser, item.meta?.access as string)) {
      return false;
    }
    return true;
  });
});
// 根据路由切换选中的菜单
const selectKey = ref(["/"]);
router.afterEach((to) => {
  selectKey.value = [to.path];
});

const doMenuClick = (key: string) => {
  router.push(key);
};
const logOut = () => {
  loginUserStore.setLoginUser({});
  router.push("/user/login");
};

const updateUser = () => {
  router.push("/update/user");
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
