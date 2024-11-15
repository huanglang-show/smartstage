import { RouteRecordRaw } from "vue-router";
import HomeView from "@/views/HomeView.vue";
import UserLoginView from "@/views/user/UserLoginView.vue";
import UserRegisterView from "@/views/user/UserRegisterView.vue";
import UserUpdateView from "@/views/user/UserUpdateView.vue";
import component from "*.vue";
import NoAuth from "@/views/NoAuth.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "home",
    component: HomeView,
    meta: {
      access: "user",
    },
  },
  {
    path: "/user/login",
    name: "userLogin",
    component: UserLoginView,
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/user/register",
    name: "userRegister",
    component: UserRegisterView,
    meta: {
      hideInMenu: true,
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
    meta: {
      access: "admin",
    },
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
