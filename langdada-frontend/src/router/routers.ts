import { RouteRecordRaw } from "vue-router";
import HomeView from "@/views/HomeView.vue";
import UserLoginView from "@/views/user/UserLoginView.vue";
import UserRegisterView from "@/views/user/UserRegisterView.vue";
import UserUpdateView from "@/views/user/UserUpdateView.vue";
import NoAuth from "@/views/NoAuth.vue";
import UserLayout from "@/layouts/UserLayout.vue";
import AdminUserView from "@/views/admin/AdminUserView.vue";
import AdminAppView from "@/views/admin/AdminAppView.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/home",
    name: "home",
    component: HomeView,
    meta: {
      access: "user",
    },
  },
  {
    path: "/user",
    name: "用户",
    component: UserLayout,
    children: [
      {
        path: "/user/login",
        name: "用户登录",
        component: UserLoginView,
      },
      {
        path: "/user/register",
        name: "用户注册",
        component: UserRegisterView,
      },
    ],
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/admin/user",
    name: "用户管理",
    component: AdminUserView,
    meta: {
      access: "admin",
    },
  },
  {
    path: "/admin/app",
    name: "应用管理",
    component: AdminAppView,
    meta: {
      access: "admin",
    },
  },
  {
    path: "/update/user",
    name: "updateUser",
    component: UserUpdateView,
    meta: {
      hideInMenu: true,
      access: "user",
    },
  },
  {
    path: "/about",
    name: "about",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/AboutView.vue"),
    meta: {},
  },
  {
    path: "/noAuth",
    name: "noAuth",
    component: NoAuth,
    meta: {
      hideInMenu: true,
    },
  },
];
export default routes;
