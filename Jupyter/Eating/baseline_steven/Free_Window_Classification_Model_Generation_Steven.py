
# coding: utf-8

# In[ ]:


import numpy as np
import scipy as sp
import os 
import sys
from sklearn.ensemble import RandomForestClassifier
from sklearn import metrics
import importlib


# In[ ]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_steven_free_utils as msfreeu
import my_classification_utils as mcu
#importlib.reload(mdu)


# In[ ]:


annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')
features = mfileu.read_file('features', 'free_features_steven_right.pkl')


# In[ ]:


def get_labels(indices, a, win_size):    
    wcount, acount = len(indices), len(a)      
    mi = indices + win_size//2    
    
    labels=np.zeros((wcount,))    
    for i in range(acount):                 
        ix1 = a[i, 0]
        ix2 = a[i, 1]
        cond = (mi>=ix1) & (mi<=ix2)
        labels[cond] = a[i, 2]
        
    return labels.astype(int)
    


# In[ ]:


def get_personal_train_features_labels(annots, features, subj, exclude_sess):
    
    x, y = [], []    
    for s in range(len(features[subj])):
        if s==sess:
            continue
        
        f = features[subj][sess][:, 1:]
        a = annots[subj][sess]
        l = get_labels(features[subj][sess][:, 0], a, 5*16)            

        if len(x)==0:
            x, y = f, l            
        else:                
            x, y = np.concatenate((x, f)), np.concatenate((y, l))            

    return x, y
    


# In[ ]:


for subj in range(len(features)):    
    for sess in range(len(features[subj])):
        print("Train for Subject, sess: ", subj, sess)
        train_x, train_y = get_personal_train_features_labels(annots=annots, features=features, subj = subj, exclude_sess=sess)
        print("Original shapes: ", train_x.shape, train_y.shape, "Neg, Meal, snack, drink:", np.sum(train_y==0), np.sum(train_y==1), np.sum(train_y==2), np.sum(train_y==3))

        train_y[train_y==2] = 1
        train_y[train_y==3] = 0
        assert np.sum(train_y>1) == 0        
        print("After label filtering:", train_x.shape, train_y.shape, "Neg, Meal, snack, drink:", np.sum(train_y==0), np.sum(train_y==1), np.sum(train_y==2), np.sum(train_y==3))

        clf = RandomForestClassifier(n_estimators =100, random_state=0, n_jobs=-1, verbose=1)
        clf.fit(train_x, train_y)    
        print("Training done!")

        mfileu.write_file('baseline_models_free_window_steven', 'free_'+str(subj)+'_'+str(sess)+'.pkl', clf)
        print("Model saved")        


# def get_lopo_train_features_labels(annots, features, exclude_subj):    
#     train_x, train_y = [], []
#     for subj in range(len(features)):        
#         if subj==exclude_subj:
#             continue
#         for sess in range(len(features[subj])):             
#             f = features[subj][sess][:, 1:]
#             a = annots[subj][sess]
#             l = get_labels(features[subj][sess][:, 0], a, 5*16)            
#                         
#             if len(train_x)==0:
#                 train_x = f
#                 train_y = l
#             else:                
#                 train_x = np.concatenate((train_x, f))
#                 train_y = np.concatenate((train_y, l))
#             
#     return train_x, train_y
#     

# subjects = sys.argv[1].split(",")
# subjects = [int(i) for i in subjects]
# print("===================== Subjects: ", subjects)
# 
# for subj in subjects:
#     print("Train for Subject: ", subj)
#     train_x, train_y = get_lopo_train_features_labels(annots=annots, features=features, exclude_subj = subj)
#     print("Original shapes: ", train_x.shape, train_y.shape, "Neg, Meal, snack, drink:", np.sum(train_y==0), np.sum(train_y==1), np.sum(train_y==2), np.sum(train_y==3))
# 
#     train_y[train_y==2] = 1
#     train_y[train_y==3] = 0
#     #cond = (train_y<=1)
#     #train_x, train_y = train_x[cond], train_y[cond]
#     print(" After label filtering:", train_x.shape, train_y.shape, "Neg, Meal, snack, drink:", np.sum(train_y==0), np.sum(train_y==1), np.sum(train_y==2), np.sum(train_y==3))
# 
#     clf = RandomForestClassifier(n_estimators =100, random_state=0, n_jobs=-1, verbose=1)
# 
#     #train_y = mcu.get_one_hot(train_y.astype(int), 2)
#     clf.fit(train_x, train_y)    
#     print("Training done!")
# 
#     mfileu.write_file('baseline_models', 'free_'+str(subj)+'.pkl', clf)
#     print("Model saved")        
# 
