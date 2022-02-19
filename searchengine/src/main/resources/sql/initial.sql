SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;


-- ----------------------------
--  Table structure for `newsthemes`
-- ----------------------------
DROP TABLE IF EXISTS `newsthemes`;
CREATE TABLE `newsthemes` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY (`name`)
) ENGINE=InnoDB;

BEGIN;
INSERT INTO `newsthemes` (`name`) VALUES
('world'),
('business'),
('politics'),
('health'),
('entertainment'),
('tech'),
('science'),
('education'),
('climate'),
('sports'),
('travel'),
('coronavirus');
COMMIT;


-- ----------------------------
--  Table structure for `news`
--  Record basic info of News
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `publish_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `head_line` tinytext,
    `news_abstract` text,
    `content` text,
    `country` varchar(255),
    `image` varchar(255),
    `theme` varchar(255),
    `url` varchar(512),
    PRIMARY KEY (`id`),
    UNIQUE KEY (`url`),
    INDEX (`url`),
    CONSTRAINT `news_theme` FOREIGN KEY (`theme`) REFERENCES `newsthemes` (`name`),
    CONSTRAINT `country` FOREIGN KEY (`country`) REFERENCES `countryList` (`countryname`)
) ENGINE=InnoDB;

# BEGIN;
# INSERT INTO `news` (`publish_date`, `head_line`, `country`, `image`, `theme`, `url`, `content` ) VALUES
# ('2022-01-06 17:11:57', 'The acquittal of four people over monument toppling is "enormously significant", historian says.', 'uk', 'http://c.files.bbci.co.uk/70E4/production/_122600982_4c9db055-d6a4-4386-a56a-010d5f1b90a0.jpg', 'world', 'https://bbc.co.uk/news/uk-england-bristol-59892211', 'The acquittal of four people charged with criminal damage after tearing down a statue of Edward Colston is a "landmark" in Britain coming to terms with its past, a TV historian said. The memorial to the slave trader was toppled during a Black Lives Matter protest in Bristol on 7 June 2020. The defendants, who are all from Bristol apart from Mr Ponsford, who is based in Hampshire, were charged after the monument to the 17th Century slave trader was removed and thrown into Bristols harbourside.'),
# ('2022-01-06 17:14:56', 'Surrey Police say investigations are continuing to find four other suspects linked to the theft.', 'uk', 'http://c.files.bbci.co.uk/1D92/production/_122607570_tommystevenson-edit.jpg', 'world', 'https://bbc.co.uk/news/uk-england-surrey-59900643', 'A thief who called out for police help after injuring himself fleeing a cash machine raid has been jailed. Tommy Stevenson, 28, hurt his leg trying to climb a fence at a BP petrol station in Horsham Road in Alfold, Surrey, at 0430 GMT on 4 December. He called out to police for help after officers arrived to find the stations cash machine damaged and £19,200 taken. Police arrived at the petrol station to find Stevenson, of of Dalmeny Way, Epsom, wearing a cap with "security" written on it. He initially claimed he was on a night-time walk.'),
# ('2022-01-06 17:15:25', 'A man who was held over an attack at Oldham police station is detained under the Mental Health Act.", historian says.', 'uk', 'http://c.files.bbci.co.uk/A08A/production/_122589014_20220105021313_ac0a2959.jpg', 'world', 'https://bbc.co.uk/news/uk-england-manchester-59897127', 'A man who was arrested on suspicion of a terrorist offence after a car was attacked at a police station has been detained under the Mental Health Act. Greater Manchester Police said a man attacked a police vehicle and made threats to officers at Oldham police station at about 22:00 GMT on Tuesday. It said a man had been detained for assessment and released without charge over the suspected terror offence. He had been initially held on suspicion of assaulting an officer, criminal damage and being found on enclosed premises.'),
# ('2022-01-06 17:24:54', 'The latest number of cases of Covid-19 across Devon and Cornwall.", historian says.', 'uk', 'http://c.files.bbci.co.uk/11281/production/_116437207_mediaitem116437205.jpg', 'world', 'https://bbc.co.uk/news/uk-england-devon-55804222', 'Here are the latest rates of cases of Covid-19 in Devon and Cornwall. The figures show the number of coronavirus cases per 100,000 people in the seven days up to and including 16 January, with the previous weeks numbers in brackets.'),
# ('2022-01-06 17:42:51', 'Cases of coronavirus reported in Kent, Sussex and Surrey.", historian says.', 'uk', 'http://c.files.bbci.co.uk/EFEA/production/_119081416_hi067970279.jpg', 'world', 'https://bbc.co.uk/news/uk-england-55133602', 'There have been more than 13,255,580 confirmed cases of coronavirus so far in England and more than 133,240 people have died, government figures show. For information about vaccines in the South East, you can click here for Kent and Medway, here for East Sussex, here for West Sussex, here for Brighton and Hove and here for Surrey. Below is a table of local authority areas in Kent, Sussex and Surrey, showing how many people have tested positive for coronavirus in the week leading up to 16 January.'),
# ('2022-01-06 18:01:35', 'The increasingly bitter dispute centres on pay received by bin lorry drivers in Coventry.", historian says.', 'us', 'http://c.files.bbci.co.uk/11AF8/production/_122604427_de15.jpg', 'world', 'https://bbc.co.uk/news/uk-england-coventry-warwickshire-59900718', 'Bin strikes in Coventry look set to last into at least March as the Unite union announced dates for further industrial action. Bin lorry drivers are calling for improved wages with the union labelling their current rates as "poverty pay".  For its part Coventry City Council hit out on Wednesday against what it described as "blatant inaccuracies" on the part of the union.'),
# ('2022-01-06 18:09:12', 'More traffic lanes and improved signalling is aimed at reducing Leeds city centre vehicle numbers.", historian says.', 'us', 'http://c.files.bbci.co.uk/1509A/production/_122607168_armleygyratory2.jpg', 'world', 'https://bbc.co.uk/news/uk-england-leeds-59900167', 'A £40m scheme to widen a main route in and out of Leeds is "critical" to cutting the number of vehicles passing through the city centre. Planned improvements to Armley Gyratory include widening roads, replacing footbridges and better signalling. The scheme, funded by the West Yorkshire Combined Authority (WYCA), will be split into two phases and should be completed next year.'),
# ('2022-01-06 18:23:28', 'Three generations of a Flitwick family, including a 13-year-old, volunteer at vaccination centres.', 'us', 'http://c.files.bbci.co.uk/0280/production/_122604600_family1.jpg', 'world', 'https://bbc.co.uk/news/uk-england-beds-bucks-herts-59896807', 'A 13-year-old Covid helper said she was "inspired" to join up after family members became volunteers. Bea, from Flitwick, Bedfordshire, helps at a Chicksands centre as part of her Duke of Edinburgh Award, alongside her working mother. She has now followed in the volunteering footsteps of her grandfather, brother and stepfather. "My mum was my biggest inspiration," she said. "I thought it was cool if I joined in as well."'),
# ('2022-01-06 18:43:09', 'The former paddle steamer, which brought 7,000 men home from France, has had her hull repainted.', 'us', 'http://c.files.bbci.co.uk/10EFA/production/_122607396_2d4a96c7-ca05-47ce-b9f4-3ebfbd3077fd.jpg', 'world', 'https://bbc.co.uk/news/uk-england-kent-59895881', 'A ship that helped rescue thousands of men from Dunkirk in the Second World War is to return to base following its latest restoration. The five-month revamp of the Medway Queen took place at Ramsgate Harbour. Built in 1924, the vessel originally took holidaymakers on trips around Kent before being commissioned by the Navy, in 1939. Pam Bathurst, of the Medway Queen Preservation Society, said: "I have got a lump in my throat, its so beautiful. She looks so gorgeous and to see her back in the sea and on her way home, its just amazing."'),
# ('2022-01-06 18:44:04', 'A man accused of murdering Raheem Hanif in Reading is only guilty of manslaughter, a jury is told.', 'us', 'http://c.files.bbci.co.uk/CDD1/production/_116898625_whatsappimage2021-02-10at17.31.03.jpg', 'world', 'https://bbc.co.uk/news/uk-england-berkshire-59901346', 'A man who took part in a fatal ambush did not intend for serious harm to come to his victim, a court has been told. Raheem Hanif was attacked with a machete in a car park in Tilehurst, Reading, on 6 February 2021. He claims he drove to the scene of the attack so his 17-year-old co-defendant, who struck the fatal stab wound, could fight Mr Hanif.');
# COMMIT;

-- ----------------------------
--  Table structure for `userItems`
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `pref_list` MEDIUMTEXT NOT NULL,
    `latest_log_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `username` varchar(255) NOT NULL,
    `password` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY (`username`)
) ENGINE=InnoDB;

BEGIN;
INSERT INTO `users` (`username`, `pref_list`, `password`) VALUES
('trump1', '', 'trump1'),
('trump2', '', 'trump2'),
('trump3', '', 'trump3'),
('trump4', '', 'trump4'),
('trump5', '', 'trump5'),
('trump6', '', 'trump6'),
('trump7', '', 'trump7'),
('trump8', '', 'trump8'),
('trump9', '', 'trump9'),
('trump10', '', 'trump10');
COMMIT;


-- ----------------------------
--  Table structure for `newslogs`
--  Record browsed history and marks from users to news
-- ----------------------------
DROP TABLE IF EXISTS `newslogs`;
CREATE TABLE `newslogs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `news_id` bigint(20) NOT NULL,
  `view_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `prefer_degree` float NOT NULL DEFAULT '0.0',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `news_id` (`news_id`),
  CONSTRAINT `newslogs_news_id` FOREIGN KEY (`news_id`) REFERENCES `news` (`id`),
  CONSTRAINT `newslogs_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB;

BEGIN;
INSERT INTO `newslogs` (`user_id`, `news_id`, `prefer_degree`) VALUES
(1, 1, 1.0),
(1, 2, 2.0),
(1, 3, 3.0),
(1, 4, 4.0),
(1, 5, 5.0),
(2, 2, 5.0),
(2, 3, 3.0),
(3, 6, 5.0),
(3, 7, 5.0),
(3, 8, 2.0),
(3, 9, 1.0),
(4, 6, 5.0),
(5, 7, 5.0),
(6, 8, 2.0),
(7, 9, 1.0);
COMMIT;


-- ----------------------------
--  Table structure for `recommendations`
-- ----------------------------
DROP TABLE IF EXISTS `recommendations`;
CREATE TABLE `recommendations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `news_id` bigint(20) NOT NULL,
  `derive_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `derive_algorithm` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `news_id` (`news_id`),
  CONSTRAINT `recommendations_news_id` FOREIGN KEY (`news_id`) REFERENCES `news` (`id`),
  CONSTRAINT `recommendations_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB;

BEGIN;
INSERT INTO `recommendations` (`user_id`, `news_id`, `derive_algorithm`) VALUES
(1, 1, 1),
(1, 2, 2),
(1, 3, 3),
(2, 4, 1),
(2, 5, 1),
(3, 6, 1),
(4, 7, 1),
(5, 8, 1),
(6, 1, 1),
(7, 1, 1),
(8, 1, 1),
(9, 1, 1);
COMMIT;

-- ----------------------------
--  Table structure for `countryList`
-- ----------------------------
DROP TABLE IF EXISTS `countryList`;
CREATE TABLE `countryList`(
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `countryname` varchar(60) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY (`countryname`)
)ENGINE=InnoDB;

BEGIN;
INSERT INTO `countryList` (`countryname`) VALUES
('None'),
('Africa'),
('Asia'),
('Middle East'),
('Europe'),
('Latin America'),
('US&Canada'),
('Afghanistan'),
('Angola'),
('Albania'),
('United Arab Emirates'),
('Argentina'),
('Armenia'),
('Antarctica'),
('French Southern and Antarctic Lands'),
('Australia'),
('Austria'),
('Azerbaijan'),
('Burundi'),
('Belgium'),
('Benin'),
('Burkina Faso'),
('Bangladesh'),
('Bulgaria'),
('The Bahamas'),
('Bosnia and Herzegovina'),
('Belarus'),
('Belize'),
('Bermuda'),
('Bolivia'),
('Brazil'),
('Brunei'),
('Bhutan'),
('Botswana'),
('Central African Republic'),
('Canada'),
('Switzerland'),
('Chile'),
('China'),
('Ivory Coast'),
('Cameroon'),
('Democratic Republic of the Congo'),
('Republic of the Congo'),
('Colombia'),
('Costa Rica'),
('Cuba'),
('Northern Cyprus'),
('Cyprus'),
('Czech Republic'),
('Germany'),
('Djibouti'),
('Denmark'),
('Dominican Republic'),
('Algeria'),
('Ecuador'),
('Egypt'),
('Eritrea'),
('Spain'),
('Estonia'),
('Ethiopia'),
('Finland'),
('Fiji'),
('Falkland Islands'),
('France'),
('Gabon'),
('United Kingdom'),
('Georgia'),
('Ghana'),
('Guinea'),
('Gambia'),
('Guinea Bissau'),
('Equatorial Guinea'),
('Greece'),
('Greenland'),
('Guatemala'),
('French Guiana'),
('Guyana'),
('Honduras'),
('Croatia'),
('Haiti'),
('Hungary'),
('Indonesia'),
('India'),
('Ireland'),
('Iran'),
('Iraq'),
('Iceland'),
('Israel'),
('Italy'),
('Jamaica'),
('Jordan'),
('Japan'),
('Kazakhstan'),
('Kenya'),
('Kyrgyzstan'),
('Cambodia'),
('South Korea'),
('Kosovo'),
('Kuwait'),
('Laos'),
('Lebanon'),
('Liberia'),
('Libya'),
('Sri Lanka'),
('Lesotho'),
('Lithuania'),
('Luxembourg'),
('Latvia'),
('Morocco'),
('Moldova'),
('Madagascar'),
('Mexico'),
('Macedonia'),
('Mali'),
('Malta'),
('Myanmar'),
('Montenegro'),
('Mongolia'),
('Mozambique'),
('Mauritania'),
('Malawi'),
('Malaysia'),
('Namibia'),
('New Caledonia'),
('Niger'),
('Nigeria'),
('Nicaragua'),
('Netherlands'),
('Norway'),
('Nepal'),
('New Zealand'),
('Oman'),
('Pakistan'),
('Panama'),
('Peru'),
('Philippines'),
('Papua New Guinea'),
('Poland'),
('Puerto Rico'),
('North Korea'),
('Portugal'),
('Paraguay'),
('Qatar'),
('Romania'),
('Russia'),
('Rwanda'),
('Western Sahara'),
('Saudi Arabia'),
('Sudan'),
('South Sudan'),
('Senegal'),
('Solomon Islands'),
('Sierra Leone'),
('El Salvador'),
('Somaliland'),
('Somalia'),
('Republic of Serbia'),
('Suriname'),
('Slovakia'),
('Slovenia'),
('Sweden'),
('Swaziland'),
('Syria'),
('Chad'),
('Togo'),
('Thailand'),
('Tajikistan'),
('Turkmenistan'),
('East Timor'),
('Trinidad and Tobago'),
('Tunisia'),
('Turkey'),
('Taiwan'),
('United Republic of Tanzania'),
('Uganda'),
('Ukraine'),
('Uruguay'),
('United States of America'),
('Uzbekistan'),
('Venezuela'),
('Vietnam'),
('Vanuatu'),
('West Bank'),
('Yemen'),
('South Africa'),
('Zambia'),
('Zimbabwe'),
('American Samoa'),
('Andorra'),
('Antigua and Barbuda'),
('Aruba'),
('Bahrain'),
('Barbados'),
('Cape Verde'),
('Comoros'),
('Cook Islands'),
('Dominica'),
('Faroe Islands'),
('Federated States of Micronesia'),
('Grenada'),
('Kingdom of the Netherlands'),
('Kiribati'),
('Liechtenstein'),
('Maldives'),
('Marshall Islands'),
('Mauritius'),
('Monaco'),
('Nauru'),
('Niue'),
('Northern Mariana Islands'),
('Palau'),
('Saint Kitts and Nevis'),
('Saint Lucia'),
('Saint Vincent and the Grenadines'),
('Samoa'),
('San Marino'),
('Seychelles'),
('Singapore'),
('Sint Maarten'),
('São Tomé and Príncipe'),
('Tonga'),
('Tuvalu'),
('Vatican City');
COMMIT;


DROP TABLE IF EXISTS `newstest`;
CREATE TABLE `newstest` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `publish_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `head_line` tinytext,
    `news_abstract` text,
    `content` text,
    `country` varchar(255),
    `image` varchar(255),
    `theme` varchar(255),
    `url` varchar(512),
    PRIMARY KEY (`id`),
    UNIQUE KEY (`url`),
    INDEX (`url`),
    CONSTRAINT `test_news_theme` FOREIGN KEY (`theme`) REFERENCES `newsthemes` (`name`),
    CONSTRAINT `test_country` FOREIGN KEY (`country`) REFERENCES `countryList` (`countryname`)
) ENGINE=InnoDB;


SET FOREIGN_KEY_CHECKS = 1;
