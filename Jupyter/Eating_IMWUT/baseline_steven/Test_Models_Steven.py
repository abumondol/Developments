
# coding: utf-8

# In[3]:


import numpy as np
import scipy as sp
import os 
import sys
import importlib
from sklearn.ensemble import RandomForestClassifier


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_steven_free_utils as msfreeu
import my_classification_utils as mcu
#importlib.reload(mdu)


# In[ ]:


features_lab = mfileu.read_file('features', 'lab_features_steven_right.pkl')
features_free = mfileu.read_file('features', 'free_features_steven_right.pkl')


# In[ ]:


for subj in range(len(features_free)):
    for sess in range(len(features_free[subj])):
        print("\nTesting free subject, sess: ", subj, sess)
        
        if subj<5:
            clf = mfileu.read_file('baseline_models_bite_steven', 'lab_'+str(subj+2)+'.pkl')
        else:
            clf = mfileu.read_file('baseline_models_bite_steven', 'lab_100.pkl')            
        
        proba = clf.predict_proba(features_free[subj][sess][:, 1:])
        proba = np.array(proba)
        res = features_free[subj][sess][:, 0].reshape((-1, 1))
        res = np.concatenate((res, proba), axis=1)        
        mfileu.write_file('baseline_proba_bite_steven', 'bite_free_'+str(subj)+'_'+str(sess)+'.pkl', res)
        
        
        clf = mfileu.read_file('baseline_models_free_window_steven', 'free_'+str(subj)+'_'+str(sess)+'.pkl')        
        proba = clf.predict_proba(features_free[subj][sess][:, 1:])
        proba = np.array(proba)
        res = features_free[subj][sess][:, 0].reshape((-1, 1))
        res = np.concatenate((res, proba), axis=1)        
        mfileu.write_file('baseline_test_proba_free_window_steven', 'window_free_'+str(subj)+'_'+str(sess)+'.pkl', res)


# for subj in range(len(features_free)):
#     for sess in range(len(features_free[subj])):
#         print("\nTesting free subject, sess: ", subj, sess)
#                 
#         clf = mfileu.read_file('baseline_models', 'free_'+str(subj)+'_'+str(sess)+'.pkl')        
#         proba = clf.predict_proba(features_free[subj][sess][:, 1:])
#         proba = np.array(proba)
#         res = features_free[subj][sess][:, 0].reshape((-1, 1))
#         res = np.concatenate((res, proba), axis=1)        
#         mfileu.write_file('baseline_test_results', 'meal_free_personal_'+str(subj)+'_'+str(sess)+'.pkl', res)

# ##################### General models for Lab data ##########################
# for subj in range(len(features_lab)):
#     for sess in range(len(features_lab[subj])):
#         print("\nTesting lab subject, sess: ", subj, sess)
#         clf = mfileu.read_file('baseline_models', 'lab_'+str(subj)+'.pkl')
#         proba = clf.predict_proba(features_lab[subj][sess][:, 1:])
#         proba = np.array(proba)
#         #print(proba[:2, 5, :2])
#         res = features_lab[subj][sess][:, 0].reshape((-1, 1))        
#         print(res.shape, proba.shape, clf.classes_)
#         res = np.concatenate((res, proba), axis=1)        
#         mfileu.write_file('baseline_test_results', 'bite_lab_'+str(subj)+'_'+str(sess)+'.pkl', res)
#         
#         if subj>=2:
#             clf = mfileu.read_file('baseline_models', 'free_'+str(subj-2)+'.pkl')
#         else:
#             clf = mfileu.read_file('baseline_models', 'free_100.pkl')
#             
#         proba = clf.predict_proba(features_lab[subj][sess][:, 1:])
#         proba = np.array(proba)
#         res = features_lab[subj][sess][:, 0].reshape((-1, 1))
#         res = np.concatenate((res, proba), axis=1)
#         mfileu.write_file('baseline_test_results', 'meal_lab_'+str(subj)+'_'+str(sess)+'.pkl', res)
#         
