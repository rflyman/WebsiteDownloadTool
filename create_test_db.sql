CREATE DATABASE 'websitedownloader_db';

USE websitedownloader_db;

CREATE USER someuser@'%' IDENTIFIED BY 'somepass';

GRANT ALL PRIVILEGES ON websitedownloader_db TO 'someuser'@'%';

FLUSH PRIVILEGES;

--
-- Database: `websitedownloader_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `domain`
--

CREATE TABLE `domain` (
  `domain_id` bigint(20) UNSIGNED NOT NULL,
  `domain_group_id` bigint(20) UNSIGNED DEFAULT NULL,
  `root_url` varchar(255) NOT NULL,
  `indexer_last_touched` bigint(20) UNSIGNED NOT NULL DEFAULT '0',
  `robots_txt_last_updated` bigint(20) UNSIGNED NOT NULL DEFAULT '0',
  `robots_txt_contents` longtext,
  `index` enum('Yes','No','Never') NOT NULL DEFAULT 'No',
  `initial_depth_to_index` int(2) NOT NULL DEFAULT '1' COMMENT 'how deep should we go.',
  `scan_priority` int(11) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `domain_group`
--

CREATE TABLE `domain_group` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `link`
--

CREATE TABLE `link` (
  `link_id` bigint(20) UNSIGNED NOT NULL,
  `link_last_found` bigint(20) UNSIGNED NOT NULL,
  `website_page_id_from` bigint(20) UNSIGNED NOT NULL,
  `website_page_id_to` bigint(20) UNSIGNED NOT NULL,
  `anchor_text` text,
  `rel` varchar(255) DEFAULT NULL,
  `title` text,
  `attr_class` varchar(255) DEFAULT NULL,
  `attr_id` varchar(255) DEFAULT NULL,
  `is_external` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `website_page`
--

CREATE TABLE `website_page` (
  `website_page_id` bigint(20) UNSIGNED NOT NULL,
  `page_url` varchar(255) NOT NULL,
  `domain_id` bigint(20) UNSIGNED NOT NULL,
  `links_last_parsed` bigint(20) UNSIGNED NOT NULL DEFAULT '0',
  `page_title` text,
  `shortest_link_depth_from_homepage` int(2) NOT NULL DEFAULT '99' COMMENT '99 is unknown',
  `index` enum('Yes','No') NOT NULL DEFAULT 'No',
  `source` longtext,
  `last_updated` int(11) NOT NULL DEFAULT '0',
  `response_message` text,
  `status_code` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `domain`
--
ALTER TABLE `domain`
  ADD PRIMARY KEY (`domain_id`),
  ADD UNIQUE KEY `root_url` (`root_url`),
  ADD KEY `index` (`index`),
  ADD KEY `domain_group_id` (`domain_group_id`),
  ADD KEY `initial_depth_to_index` (`initial_depth_to_index`),
  ADD KEY `robots_txt_last_updated` (`robots_txt_last_updated`),
  ADD KEY `indexer_last_touched` (`indexer_last_touched`);

--
-- Indexes for table `domain_group`
--
ALTER TABLE `domain_group`
  ADD PRIMARY KEY (`id`),
  ADD KEY `name` (`name`);

--
-- Indexes for table `link`
--
ALTER TABLE `link`
  ADD PRIMARY KEY (`link_id`),
  ADD KEY `website_page_id_from` (`website_page_id_from`),
  ADD KEY `website_page_id_to` (`website_page_id_to`),
  ADD KEY `is_external` (`is_external`),
  ADD KEY `link_last_found` (`link_last_found`);

--
-- Indexes for table `website_page`
--
ALTER TABLE `website_page`
  ADD PRIMARY KEY (`website_page_id`),
  ADD KEY `links_last_updated` (`links_last_parsed`),
  ADD KEY `depth` (`shortest_link_depth_from_homepage`,`index`),
  ADD KEY `domain_id` (`domain_id`),
  ADD KEY `index` (`index`),
  ADD KEY `shortest_link_depth_from_homepage` (`shortest_link_depth_from_homepage`),
  ADD KEY `page_url` (`page_url`) USING BTREE;

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `domain`
--
ALTER TABLE `domain`
  MODIFY `domain_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=45792;
--
-- AUTO_INCREMENT for table `domain_group`
--
ALTER TABLE `domain_group`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `link`
--
ALTER TABLE `link`
  MODIFY `link_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `website_page`
--
ALTER TABLE `website_page`
  MODIFY `website_page_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15468;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `domain`
--
ALTER TABLE `domain`
  ADD CONSTRAINT `domain_ibfk_1` FOREIGN KEY (`domain_group_id`) REFERENCES `domain_group` (`id`);

--
-- Constraints for table `link`
--
ALTER TABLE `link`
  ADD CONSTRAINT `link_ibfk_1` FOREIGN KEY (`website_page_id_from`) REFERENCES `website_page` (`website_page_id`),
  ADD CONSTRAINT `link_ibfk_2` FOREIGN KEY (`website_page_id_to`) REFERENCES `website_page` (`website_page_id`);

--
-- Constraints for table `website_page`
--
ALTER TABLE `website_page`
  ADD CONSTRAINT `website_page_ibfk_1` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`domain_id`);

INSERT INTO `domain` (`domain_id`, `domain_group_id`, `root_url`, `indexer_last_touched`, `robots_txt_last_updated`, `robots_txt_contents`, `index`, `initial_depth_to_index`, `scan_priority`) VALUES (NULL, NULL, 'http://refsalessystems.com', '0', '0', NULL, 'Yes', '1', '1');
