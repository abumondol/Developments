
# coding: utf-8

# In[ ]:


import numpy as np
import os 
import sys
from sklearn.ensemble import RandomForestClassifier
from sklearn import metrics
import importlib


# In[ ]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_classification_utils as mcu
#importlib.reload(mdu)


# In[ ]:


train_test = 'train'
if 'C:' not in mfileu.get_path():    
    train_test = sys.argv[1]
    
assert train_test in ['train', 'test_free', 'test_lab']


# In[ ]:


#process lab_features
fs_lab = mfileu.read_file('features/features_step2', 'lab_features_steven_right.pkl')
fs_free = mfileu.read_file('features/features_step2', 'free_features_steven_right.pkl')
ssilvg_lab = mfileu.read_file('ssilvg/ssilvg_step2', 'lab_ssilvg_steven_right.pkl')
ssilvg_free = mfileu.read_file('ssilvg/ssilvg_step2', 'free_ssilvg_steven_right.pkl')


# In[ ]:


features_lab_selected, ssil_lab_selected= np.empty((0, 32)), np.empty((0, 4))
for subj in range(len(ssilvg_lab)):
    for sess in range(len(ssilvg_lab[subj])):
        ix = ssilvg_lab[subj][sess]
        l = ix[:, 3]
        v = ix[:, 4]
        g = ix[:, 5]
        cond = (l>=0) & (v>=1) & (g<=-2) & (g>=-10)
        
        ix = ix[cond, :4]
        f = fs_lab[subj][sess][cond, 1:]
        ssil_lab_selected =  np.concatenate((ssil_lab_selected, ix))
        features_lab_selected = np.concatenate((features_lab_selected, f))

cond = (ssil_lab_selected[:, -1]>=2)
ssil_lab_selected[cond, -1] = 0
assert len(features_lab_selected) == len(ssil_lab_selected)

print("Lab selected fetures, ssil shape:", features_lab_selected.shape, ssil_lab_selected.shape)

l = ssil_lab_selected[:, -1]
print("Lab gt Neg, Bite, sip:", np.sum(l==0), np.sum(l==1), np.sum(l==2
                                                                  ))


# In[ ]:


features_free_selected, ssil_free_selected= np.empty((0, 32)), np.empty((0, 4))
for subj in range(len(ssilvg_free)):
    for sess in range(len(ssilvg_free[subj])):
        ix = ssilvg_free[subj][sess]
        l = ix[:, 3]
        v = ix[:, 4]
        g = ix[:, 5]
        cond = ((l==0) | (l==3)) & (v>=1) & (g<=-2) & (g>=-10)
        
        ix = ix[cond, :4]
        f = fs_free[subj][sess][cond, 1:]
        ssil_free_selected =  np.concatenate((ssil_free_selected, ix))
        features_free_selected = np.concatenate((features_free_selected, f))

cond = (ssil_free_selected[:, -1]==3)
ssil_free_selected[cond, -1] = 0

assert len(features_free_selected) == len(ssil_free_selected)        
assert np.sum(ssil_free_selected[:, -1]) == 0

print("Free selected fetures, ssil shape:", features_free_selected.shape, ssil_free_selected.shape)


# In[ ]:


total = 0
for subj in range(len(ssilvg_free)):
    for sess in range(len(ssilvg_free[subj])):
        total += len(ssilvg_free[subj][sess])
print("Total original free: ", total)


# In[ ]:


def get_train_xy(subj):
    
    exclude_subj = -1 if subj>=5 else subj+2
    cond = (ssil_lab_selected[:, 0]!=exclude_subj)    
    xlab = features_lab_selected[cond, :]
    ylab = ssil_lab_selected[cond, -1]
    
    cond = (ssil_free_selected[:, 0]!=subj)    
    xfree = features_free_selected[cond, :]
    yfree = ssil_free_selected[cond, -1]
    
    train_x = np.concatenate((xlab, xfree))
    train_y = np.concatenate((ylab, yfree))
    
    assert len(train_x) == len(train_y)    
    return train_x, train_y


# In[ ]:


if train_test=='train':
    for subj in range(11):    
        print("Train Subject: ", subj)
        
        train_x, train_y = get_train_xy(subj)                
        print("Subj:", subj, ", shapes:", train_x.shape, train_y.shape, "Neg, Bite, sip:", np.sum(train_y==0), np.sum(train_y==1))

        clf = RandomForestClassifier(n_estimators =100, random_state=0, n_jobs=-1, verbose=1)    
        clf.fit(train_x, train_y)    
        print("Training done!")

        mfileu.write_file('bite_models_rf/g2', 'subj_'+str(subj)+'.pkl', clf)
        print("Model saved")


# In[ ]:


if train_test=='test_free':       
    print("Testing Free ..... ")    
    ba = mfileu.read_file('data', 'free_data_steven_blank_array.pkl')
    
    for subj in range(11):        
        clf = mfileu.read_file('bite_models_rf/g2', 'subj_'+str(subj)+'.pkl') 
        for sess in range(len(ssilvg_free[subj])):            
            print(subj, sess)
            test_x = fs_free[subj][sess][:, 1:]
            proba = clf.predict_proba(test_x)
            proba = np.array(proba)
            proba = proba[:, 1].reshape((-1, 1))
            
            ix = ssilvg_free[subj][sess]
            l = ix[:, 3]
            v = ix[:, 4]
            g = ix[:, 5]
     
            cond = (v>=1) & (g<=-3) & (g>=-10)
            cond = np.logical_not(cond)
            proba[cond, :] = 0
            ssilp = np.concatenate((ix[:, :4], proba), axis=1)
            ba[subj][sess] = ssilp
        
    mfileu.write_file('classification_results', 'free_ssilp_rf_g2.pkl', ba)
    
    

