/**
 * Copyright (c) 2017-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

// See https://docusaurus.io/docs/site-config for all the possible
// site configuration options.

// List of projects/orgs using your project for the users page.
const users = [
  {
    caption: 'User1',
    // You will need to prepend the image path with your baseUrl
    // if it is not '/', like: '/test-site/img/image.jpg'.
    image: '/img/undraw_open_source.svg',
    infoLink: 'https://www.facebook.com',
    pinned: true,
  },
];

const siteConfig = {
  title: 'Massimiliano Ziccardi - JNRPE', // Title for your website.
  tagline: 'Java Nagios Remote Plugin Executor',
  url: 'https://www.jnrpe.it', // Your website URL
  baseUrl: '/jnrpe/', // Base URL for your project */
  // For github.io type URLs, you would set the url and baseUrl like:
  //   url: 'https://facebook.github.io',
  //   baseUrl: '/test-site/',

  // Used for publishing and more
  projectName: 'jnrpe',
  organizationName: 'ziccardi',
  // For top-level user or org sites, the organization is still the same.
  // e.g., for the https://JoelMarcey.github.io site, it would be set like...
  //   organizationName: 'JoelMarcey'

  stylesheets: [
    "https://fonts.googleapis.com/css?family=Muli&display=swap",
    "/css/code-block-buttons.css"
  ],

  // For no header links in the top nav bar -> headerLinks: [],
  headerLinks: [
    { href: 'https://github.com/ziccardi/jnrpe', label: 'GitHUB'},
    { href: 'https://www.jnrpe.it', label: 'Home'},
    // {page: 'help', label: 'Help'},
    {blog: true, label: 'Blog'},
  ],

  // If you have users set above, you add it here:
  users,

  /* path to images for header/footer */
  headerIcon: '',
  footerIcon: '',
  favicon: 'img/favicon.ico',

  /* Colors for website */
  colors: {
    primaryColor: '#1981BE',
    secondaryColor: '#81BED7',
  },

  /* Custom fonts for website */

  fonts: {
    myFont: [
      "Times New Roman",
      "Serif"
    ],
    myOtherFont: [
      "-apple-system",
      "system-ui"
    ]
  },


  // This copyright info is used in /core/Footer.js and blog RSS/Atom feeds.
  copyright: `Copyright © ${new Date().getFullYear()} Massimiliano Ziccardi`,

  highlight: {
    // Highlight.js theme to use for syntax highlighting in code blocks.
    theme: 'dark',
  },

  // Add custom scripts here that would be placed in <script> tags.
  scripts: ['https://buttons.github.io/buttons.js'],

  // On page navigation for the current documentation page.
  onPageNav: 'separate',
  // No .html extensions for paths.
  cleanUrl: true,

  // Open Graph and Twitter card images.
  ogImage: 'img/undraw_online.svg',
  twitterImage: 'img/undraw_tweetstorm.svg',

  markdownPlugins: [
    // Highlight admonitions (callouts such as tips, warnings, note, important, etc)
    require('remarkable-admonitions')({ icon: 'svg-inline' })
  ],

  // For sites with a sizable amount of content, set collapsible to true.
  // Expand/collapse the links and subcategories under categories.
  docsSideNavCollapsible: true,

  // Show documentation's last contributor's name.
  // enableUpdateBy: true,

  // Show documentation's last update time.
  // enableUpdateTime: true,

  // You may provide arbitrary config keys to be used as needed by your
  // template. For example, if you need your repo's URL...
  repoUrl: 'https://github.com/ziccardi/jnrpe',
  mailingList: 'https://groups.google.com/forum/#!forum/js-jnrpe',
};

module.exports = siteConfig;
