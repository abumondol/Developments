
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


source = 'steven'
assert source in ['steven', 'combo']
fs = mfileu.read_file('features', 'lab_features_{}_right.pkl'.format(source))
ssilv = mfileu.read_file('ssilv', 'lab_ssilv_{}_right.pkl'.format(source))
print(" =================== Data source: ", source, len(fs), len(ssilv))


# In[ ]:


def get_lopo_train_features_labels(exclude_subj, step=8):    
    train_x, train_y, var = [], [], []
    subj_count = len(fs)
    
    for subj in range(subj_count):        
        if subj==exclude_subj:
            continue
        for sess in range(len(fs[subj])):             
            f = fs[subj][sess][::step, 1:]            
            l = ssilv[subj][sess][::step, 3]
            v = ssilv[subj][sess][::step, 4]
            assert len(f)==len(l)
            
            if len(train_x)==0:
                train_x, train_y, var = f, l, v
            else:
                train_x, train_y, var =  np.concatenate((train_x, f)), np.concatenate((train_y, l)),  np.concatenate((var, v)) 
    
    assert len(train_x) == len(train_y) == len(var)
    train_y = train_y.astype(int)
    return train_x, train_y, var
    


# In[ ]:


step = 8
for subj in [0, 1, 2, 3, 4, 5, 6, 100]:
    #subj = int(sys.argv[1])
    print("Train Subject: ", subj)
        
    train_x, train_y, var = get_lopo_train_features_labels(exclude_subj=subj, step=step)
    print("Subj:", subj, ", shapes:", train_x.shape, train_y.shape, "X, Neg, Bite, sip:",  np.sum(train_y==-1), np.sum(train_y==0), np.sum(train_y==1), np.sum(train_y==2))

    cond = (var>=1) & (var<=25)
    train_x, train_y = train_x[cond], train_y[cond]
    print("Subj:", subj, ", shapes:", train_x.shape, train_y.shape, "X, Neg, Bite, sip:", np.sum(train_y==-1), np.sum(train_y==0), np.sum(train_y==1), np.sum(train_y==2))
        
    train_y[train_y>=2] = 0
    train_y[train_y<0] = 0
    print("Subj:", subj, ", shapes:", train_x.shape, train_y.shape, "X, Neg, Bite, sip:", np.sum(train_y==-1), np.sum(train_y==0), np.sum(train_y==1), np.sum(train_y==2))

    clf = RandomForestClassifier(n_estimators =100, random_state=0, n_jobs=-1, verbose=1)    
    clf.fit(train_x, train_y)    
    print("Training done!")

    mfileu.write_file('bite_models/RF_F', 'lab_'+str(subj)+'.pkl', clf)
    print("Model saved")

