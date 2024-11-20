import router from "@/router";
import { useLoginUserStore } from "@/store/userStore";
import ACCESS_ENUM from "@/access/accessEnum";
import checkAccess from "@/access/checkAccess";

/**
 * 访问页面前，进行权限校验
 */
router.beforeEach((to, from, next) => {
  const userLoginStore = useLoginUserStore();
  // 当前登录用户信息
  const loginUser = userLoginStore.loginUser;
  // 页面需要的权限
  const needAccess = (to.meta?.access as string) ?? ACCESS_ENUM.NOT_LOGIN;
  // 需要登录的页面
  if (needAccess !== ACCESS_ENUM.NOT_LOGIN) {
    // 如果当前登录用户信息不存在，则跳转到登录页面
    if (!loginUser || loginUser === ACCESS_ENUM.NOT_LOGIN) {
      next("/user/login");
      return;
    }
    // 如果当前登录用户信息存在，则判断是否有权限访问该页面
    if (!checkAccess(loginUser, needAccess)) {
      next("/noAuth");
      return;
    }
  }
  next();
});
