# scala_final_project

Final_FB_recommend_UI is our final project application, it is a web application built with play framework,
activator used as the build tool.

To run this application:
1.Simply install activator and unzip
2.Change directory to activator bin
3.Type in activator ui in command window and run the application.


Four main functions have been implemented for this web application:




1.loading data(fetch whole time line posts of each user and store single user's posts as a text file,
               as format of posts has been resolved during the fetching procedure, so when tokenizing 
               documents, we simply use non-literate symbol and remove some stop words and numbers)
               
2. build graph(build visualize graph given a user id as the beginning node, user/likes is treated as the edge to
               connect to assocative users, with depth of three, graph contains more than 20 thousands
               nodes. )
               
3. tf-idf analysis(input is a single uer id, the output will be 10 recommended users for the give user)

4. search user(input is a keyword, the output will be users who has same keyword extracted from tfidf analysis )


Final_Project_Merge folder is draft code, it contains all  implementation and test cases.







