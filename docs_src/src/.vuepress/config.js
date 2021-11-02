const { description } = require('../../package')

module.exports = {
  /**
   * Ref：https://v1.vuepress.vuejs.org/config/#title
   */
  title: 'Concord',
  /**
   * Ref：https://v1.vuepress.vuejs.org/config/#description
   */
  description: description,

  /**
   * Extra tags to be injected to the page HTML `<head>`
   *
   * ref：https://v1.vuepress.vuejs.org/config/#head
   */
  head: [
    ['meta', { name: 'theme-color', content: '#3eaf7c' }],
    ['meta', { name: 'apple-mobile-web-app-capable', content: 'yes' }],
    ['meta', { name: 'apple-mobile-web-app-status-bar-style', content: 'black' }]
  ],

  /**
   * Theme configuration, here is the default theme configuration for VuePress.
   *
   * ref：https://v1.vuepress.vuejs.org/theme/default-theme-config.html
   */
  themeConfig: {
    repo: '',
    editLinks: false,
    docsDir: '',
    editLinkText: '',
    lastUpdated: false,
    nav: [
      { text: 'Server', link: '/server/' },
	  {
		text: 'API',
		ariaLabel: 'API Menu',
		items: [
			{ text: 'REST', link: '/rest-api/' },
			{ text: 'Websocket', link: '/websocket-api/' }
		]
	  },
	  { text: 'GitHub', link: 'https://github.com/Concord-Communication' }
    ],
	logo: '/concord_icon.png',
	sidebar: {
      '/server/': ['', 'quick-start'],
	  '/rest-api/': 'auto',
	  '/websocket-api/': ['', 'chat-messages']
    },
	smoothScroll: true
  },

  /**
   * Apply plugins，ref：https://v1.vuepress.vuejs.org/zh/plugin/
   */
  plugins: [
    '@vuepress/plugin-back-to-top',
    '@vuepress/plugin-medium-zoom',
  ]
}
