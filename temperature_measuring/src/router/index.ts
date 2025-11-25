import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [{
      path: '/',
      name: 'login',
      component: () => import('../views/Login.vue'),
    },
	
     {
					  path: '/first',
					  name: 'first',
					  component: () => import('../views/first.vue'),
     },
     {
					  path: '/dashboard',
					  name: 'dashboard',
					  component: () => import('../views/Dashboard.vue'),
			},
      {
				  	path: '/simulate',
				  	name: 'simulate',
				  	component: () => import('../views/Simulate.vue'),
			},
      {
				    path: '/profile',
				  	name: 'profile',
				  	component: () => import('../views/Profile.vue'),
			},
  
  ],
})

export default router
