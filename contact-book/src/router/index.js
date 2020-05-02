import Vue from 'vue'
import VueRouter from 'vue-router'
// import Contact from '@/views/Contact.vue'

Vue.use(VueRouter)

  const routes = [
  {
    path: '/',
    name: 'Contact Book',
    component: () => import(/* webpackChunkName: "contact" */ '@/views/Contact.vue'),
    props: true,
  },
  {
    path: '/contact-detail',
    name: 'Contact detail',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "contact-details" */ '@/views/ContactDetail.vue'),
    props: true
  }
]

const router = new VueRouter({
  // mode: "history",
  // base: '/app/',
  routes
});

export default router
