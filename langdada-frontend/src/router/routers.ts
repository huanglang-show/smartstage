import { RouteRecordRaw } from "vue-router";
import HomeView from "@/views/HomeView.vue";
import UserLoginView from "@/views/user/UserLoginView.vue";
import UserRegisterView from "@/views/user/UserRegisterView.vue";
import UserUpdateView from "@/views/user/UserUpdateView.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "home",
    component: HomeView,
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
    },
  },
  {
    path: "/about",
    name: "about",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/AboutView.vue"),
  },
];
export default routes;
