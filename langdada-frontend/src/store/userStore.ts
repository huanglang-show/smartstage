import { defineStore } from "pinia";
import { ref } from "vue";
import { getLoginUserUsingGet } from "@/api/userController";
import API from "@/api/typings";

export const useLoginUserStore = defineStore(
  "loginUser",
  () => {
    const loginUser = ref<API.LoginUserVO>({
      userName: "未登录",
    });

    async function fetchLoginUser() {
      const res = await getLoginUserUsingGet();
      if (res.data.code === 0 && res.data.data) {
        setLoginUser(res.data.data);
      }
    }

    function setLoginUser(newLoginUser: API.LoginUserVO) {
      loginUser.value = newLoginUser;
    }

    function deleteLoginUser() {
      loginUser.value = {};
    }

    return { loginUser, setLoginUser, fetchLoginUser, deleteLoginUser };
  },
  {
    // 持久化配置
    persist: {
      enabled: true,
      strategies: [
        {
          key: "loginUser", // 持久化的键名
          storage: localStorage, // 存储位置，默认为 localStorage
          paths: ["loginUser"], // 需要持久化的状态路径数组}
        },
      ],
    },
  }
);
