/**
 * Copyright (c) 2017-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

const React = require('react');

const CompLibrary = require('../../core/CompLibrary.js');

const Container = CompLibrary.Container;
const GridBlock = CompLibrary.GridBlock;

function Help(props) {
  const {config: siteConfig, language = ''} = props;
  const {baseUrl, docsUrl} = siteConfig;
  const docsPart = `${docsUrl ? `${docsUrl}/` : ''}`;
  const langPart = `${language ? `${language}/` : ''}`;
  const docUrl = doc => `${baseUrl}${docsPart}${langPart}${doc}`;

  const supportLinks = [
    {
      content: `Learn more about JNRPE using the [official documentation](${docUrl(
          'overview',
      )}).`,
      title: 'Browse the docs',
    },
    {
      content: 'You can follow and contact us on [Twitter](https://twitter.com/jnrpe1).',
      title: 'Twitter',
    },
    {
      content: 'At our [GitHub repo](https://github.com/ziccardi/jnrpe) Browse and submit ' +
          '[issues](https://github.com/ziccardi/jnrpe/issues) ' +
          'or [pull requests](https://github.com/ziccardi/jnrpe/pulls) for bugs you find or ' +
          'any new features you may want implemented.',
      title: 'GitHub'
    },
    {
      content: 'If you’ve got ideas for the JNRPE project or want to share what you\'re working on or struggling with, ' +
          'the <a href="https://groups.google.com/forum/#!forum/js-jnrpe">JNRPE Google Group</a> is a good place to ' +
          'start. The Mailing List is used for both team-wide and community communication.',
      title: 'Join the community',
    },
  ];


  return (
      <div className="docMainWrapper wrapper">
        <Container className="mainContainer documentContainer postContainer">
          <div className="post">
            <header className="postHeader">
              <h1>Need help?</h1>
            </header>
            <p>This project is maintained by a dedicated group of people.</p>
            <GridBlock contents={supportLinks} layout="fourColumn" />
          </div>
        </Container>
      </div>
  );
}

module.exports = Help;