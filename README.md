# LACMS - Looked After Child Management System
Looked After Child Management System is a Spring Boot application using Google Firebase for user authentication and data storage. 

The current management system for looked after children has many flaws with handling care notes as it is paper-based. Once looked-after children have left the care system, they receive a box of paper notes with some notes belonging to siblings. Furthermore, social workers are frequently changing, which makes it difficult for the children to know who to contact when in need.
LACMS aims to combat these issues with an online application for storing and managing meeting notes with real-time updates for social worker contact information.

LACMS authenticates the users using Firebase Authenticate Rest API. After authentication, users can log new meeting notes and view, edit, and delete previous meeting notes. There are three types of users: social workers, social workers managers, and looked-after children. A social worker manager user can assign looked-after children to social workers. Social worker users can add meeting notes for looked-after child users, update and delete meeting notes, and add comments to meeting notes. Looked-after child users can archive notes and add comments.

## Installation

After installation retrieve the Google Firebase Service Account Key from [Google Firebase](https://console.firebase.google.com/u/0/) and store it in src/main/resources/ within the project. 

