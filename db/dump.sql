CREATE DATABASE  IF NOT EXISTS `various_topics_forum` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `various_topics_forum`;
-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: various_topics_forum
-- ------------------------------------------------------
-- Server version	9.0.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` bigint NOT NULL,
  `content` varchar(1000) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL COMMENT 'Soft delete indicator',
  `updated_at` datetime(6) NOT NULL,
  `discussion_id` bigint DEFAULT NULL,
  `parent_comment_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKb946tdregae9v16q8i2clm2ft` (`discussion_id`),
  KEY `FKhvh0e2ybgg16bpu229a5teje7` (`parent_comment_id`),
  KEY `FK8kcum44fvpupyw6f5baccx25c` (`user_id`),
  CONSTRAINT `FK8kcum44fvpupyw6f5baccx25c` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKb946tdregae9v16q8i2clm2ft` FOREIGN KEY (`discussion_id`) REFERENCES `discussion` (`id`),
  CONSTRAINT `FKhvh0e2ybgg16bpu229a5teje7` FOREIGN KEY (`parent_comment_id`) REFERENCES `comment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,'Wildlife conservation is essential for keeping our ecosystems healthy and diverse. One effective approach is creating protected areas where wildlife can thrive without human interference. Success stories, like the recovery of certain endangered species, show that concerted conservation efforts can make a real difference. Individuals can contribute by supporting conservation groups, participating in local projects, and spreading awareness about the importance of protecting wildlife. Every effort helps in the ongoing mission to preserve the planet\'s rich biodiversity.','2024-07-25 21:35:32.823000',NULL,'2024-07-25 21:35:32.823000',4,NULL,2),(2,'Urban green spaces are vital for city life. They improve air quality, provide recreation, and help reduce heat. Investing in more parks and green areas can greatly enhance community well-being.','2024-07-25 21:36:30.935000',NULL,'2024-07-25 21:36:30.935000',5,NULL,3),(3,'Exactly, urban green spaces are key for healthier cities. They offer recreation, improve air quality, and support biodiversity. Examples like community gardens show their positive impact on residents\' lives.','2024-07-25 21:38:05.559000','2024-07-25 21:38:41.381000','2024-07-25 21:38:41.382000',5,NULL,2),(4,'Exactly, urban green spaces are key for healthier cities. They offer recreation, improve air quality, and support biodiversity. Examples like community gardens show their positive impact on residents\' lives.','2024-07-25 21:38:54.582000',NULL,'2024-07-25 21:38:54.582000',5,2,2),(5,'Absolutely. Expanding green spaces can lead to more vibrant, resilient cities and better overall quality of life for residents.','2024-07-25 21:39:32.747000',NULL,'2024-07-25 21:39:32.747000',5,4,3);
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment_seq`
--

DROP TABLE IF EXISTS `comment_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_seq` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_seq`
--

LOCK TABLES `comment_seq` WRITE;
/*!40000 ALTER TABLE `comment_seq` DISABLE KEYS */;
INSERT INTO `comment_seq` VALUES (101);
/*!40000 ALTER TABLE `comment_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment_vote`
--

DROP TABLE IF EXISTS `comment_vote`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_vote` (
  `is_upvote` bit(1) NOT NULL,
  `comment_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`comment_id`,`user_id`),
  KEY `FKmt5fid2gcsvos5bn2gy2vmbrn` (`user_id`),
  CONSTRAINT `FKmt5fid2gcsvos5bn2gy2vmbrn` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKsuhgx7catnt6chnndede0wmpr` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_vote`
--

LOCK TABLES `comment_vote` WRITE;
/*!40000 ALTER TABLE `comment_vote` DISABLE KEYS */;
INSERT INTO `comment_vote` VALUES (_binary '',1,3),(_binary '',2,2),(_binary '',4,3),(_binary '',5,2);
/*!40000 ALTER TABLE `comment_vote` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `discussion`
--

DROP TABLE IF EXISTS `discussion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discussion` (
  `id` bigint NOT NULL,
  `category` enum('ARTS_AND_LITERATURE','BUSINESS_AND_ECONOMICS','EDUCATION','ENTERTAINMENT','ENVIRONMENT','FOOD_AND_DRINK','HEALTH_AND_WELLNESS','HISTORY_AND_CULTURE','NATURE_AND_ANIMALS','OTHER','PHILOSOPHY_AND_RELIGION','POLITICS_AND_SOCIETY','SCIENCE','SPORTS_AND_FITNESS','TECHNOLOGY','TRAVEL') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL COMMENT 'Soft delete indicator',
  `description` varchar(1000) NOT NULL,
  `title` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2t0n9y82hsec0vpyll58gu4ph` (`user_id`),
  CONSTRAINT `FK2t0n9y82hsec0vpyll58gu4ph` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discussion`
--

LOCK TABLES `discussion` WRITE;
/*!40000 ALTER TABLE `discussion` DISABLE KEYS */;
INSERT INTO `discussion` VALUES (1,'TECHNOLOGY','2024-07-25 21:31:18.797000',NULL,'With the rollout of 5G networks gaining momentum, I\'m curious about how this technology will impact our daily lives. What are the potential benefits and changes we can expect in areas like mobile connectivity, smart devices, and data speeds? How will 5G affect various industries and services?','How will 5G technology transform everyday life?','2024-07-25 21:31:18.797000',2),(2,'FOOD_AND_DRINK','2024-07-25 21:33:44.761000',NULL,'I\'m preparing a fruit salad for a gathering and want to make sure it’s both refreshing and flavorful. What are some great fruit combinations that work well together? How do I ensure the fruits stay fresh? Any tips on additional ingredients that can enhance the flavor? I’d love to hear your ideas and suggestions for creating a fantastic fruit salad!','Seeking advice on making a delicious fruit salad','2024-07-25 21:33:44.761000',3),(3,'HISTORY_AND_CULTURE','2024-07-25 21:34:16.821000',NULL,'The industrial revolution was a major turning point in history. In what ways do you think it has shaped the world we live in today? Are there aspects of modern society that can be directly linked to the changes that occurred during that period?','How did the industrial revolution shape modern society?','2024-07-25 21:34:16.821000',2),(4,'NATURE_AND_ANIMALS','2024-07-25 21:34:40.035000',NULL,'Wildlife conservation plays a crucial role in maintaining the health of our ecosystems and preserving biodiversity. What are some effective strategies and success stories in wildlife conservation? How can individuals and communities contribute to these efforts? Share your thoughts on the importance of protecting our planet’s wildlife and the challenges involved.','The importance of wildlife conservation in preserving biodiversity','2024-07-25 21:34:40.035000',3),(5,'ENVIRONMENT','2024-07-25 21:35:08.351000',NULL,'Urban green spaces, such as parks and community gardens, play a crucial role in improving city life. How do these spaces contribute to environmental sustainability and residents’ well-being? Share your thoughts on the benefits of green spaces and any successful examples of urban greening projects.','The role of urban green spaces in enhancing city life','2024-07-25 21:35:08.351000',2);
/*!40000 ALTER TABLE `discussion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `discussion_seq`
--

DROP TABLE IF EXISTS `discussion_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discussion_seq` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discussion_seq`
--

LOCK TABLES `discussion_seq` WRITE;
/*!40000 ALTER TABLE `discussion_seq` DISABLE KEYS */;
INSERT INTO `discussion_seq` VALUES (101);
/*!40000 ALTER TABLE `discussion_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `discussion_vote`
--

DROP TABLE IF EXISTS `discussion_vote`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discussion_vote` (
  `is_upvote` bit(1) NOT NULL,
  `discussion_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`discussion_id`,`user_id`),
  KEY `FKd136a1wbm1y1v17j1270b0uod` (`user_id`),
  CONSTRAINT `FKd136a1wbm1y1v17j1270b0uod` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKikjqo7l4botnb30ayadurac0d` FOREIGN KEY (`discussion_id`) REFERENCES `discussion` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discussion_vote`
--

LOCK TABLES `discussion_vote` WRITE;
/*!40000 ALTER TABLE `discussion_vote` DISABLE KEYS */;
INSERT INTO `discussion_vote` VALUES (_binary '',4,2),(_binary '',5,3);
/*!40000 ALTER TABLE `discussion_vote` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `is_enabled` bit(1) NOT NULL,
  `password` varchar(255) NOT NULL,
  `profile_name` varchar(255) NOT NULL,
  `role` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`),
  CONSTRAINT `user_chk_1` CHECK ((`role` between 0 and 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--


-- Admin password: 531hurfj9a
-- User1 password: vnu2775nr1
-- User2 password: ztcz8ugne3
LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'2024-07-25 21:29:52.363000','admin@example.com',_binary '','$2a$10$F3NDjMvU32dbYE4RTBtBEuXFdTXIt6yf1waTb9kgKuad6jNvuBATu','Admin',0),(2,'2024-07-25 21:30:09.057000','user1@example.com',_binary '','$2a$10$WXO0x05SkKWWlanuTtMgKuFx2gWKwJ8dgsgYieb7hZJkviDJ3n5pi','User1',1),(3,'2024-07-25 21:30:24.976000','user2@example.com',_binary '','$2a$10$PieTVHPgu2qkztlQkipcze6vWpgpvF8zvlz4Lw4grKpRv4lGZQ6wW','User2',1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_seq`
--

DROP TABLE IF EXISTS `user_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_seq` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_seq`
--

LOCK TABLES `user_seq` WRITE;
/*!40000 ALTER TABLE `user_seq` DISABLE KEYS */;
INSERT INTO `user_seq` VALUES (101);
/*!40000 ALTER TABLE `user_seq` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-07-25 23:42:08
