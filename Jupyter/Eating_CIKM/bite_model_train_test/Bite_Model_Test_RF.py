
# coding: utf-8

# In[ ]:


import numpy as np
import scipy as sp
import os 
import sys
import importlib
from sklearn.ensemble import RandomForestClassifier


# In[ ]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_steven_free_utils as msfreeu
import my_classification_utils as mcu
#importlib.reload(mdu)


# In[ ]:


model, lab_free = sys.argv[1], sys.argv[2]
print("Model, lab_free: ", model, lab_free)


# In[ ]:


########## Test Free Data for bite
if lab_free=='free':
    fs = mfileu.read_file('features', 'free_features_steven_right.pkl')
    ssilv = mfileu.read_file('ssilv', 'free_ssilv_steven_right.pkl')
    ba = mfileu.read_file('data', 'free_data_steven_blank_array.pkl')
    

    for subj in range(len(ssilv)):
        for sess in range(len(ssilv[subj])):
            print("\nTesting free subject, sess: ", subj, sess)

            if subj<5:
                clf = mfileu.read_file('bite_models/'+model, 'lab_'+str(subj+2)+'.pkl')
            else:
                clf = mfileu.read_file('bite_models/'+model, 'lab_100.pkl')            

            proba = clf.predict_proba(fs[subj][sess][:, 1:])
            proba = np.array(proba)
            proba = proba[:, 1]
            
            gt = ssilv[subj][sess][:, 3]
            ssilv[subj][sess][:, 3] = proba                    
            ba[subj][sess] = ssilv[subj][sess][:, 2:]            
            print("Proba shape, pos, gtpos:", proba.shape, np.sum(proba>=0.5), np.sum(gt==1))

    mfileu.write_file('all_proba', 'all_proba_bite_free_{}.pkl'.format(model), ba)


# In[ ]:


########Test Lab data for bite##########################
if lab_free=='lab':
    fs = mfileu.read_file('features', 'lab_features_steven_right.pkl')    
    ssilv = mfileu.read_file('ssilv', 'lab_ssilv_steven_right.pkl')
    ba = mfileu.read_file('data', 'lab_data_steven_blank_array.pkl')

    for subj in range(len(ssilv)):
        for sess in range(len(ssilv[subj])):
            print("\nTesting lab subject, sess: ", subj, sess)

            clf = mfileu.read_file('bite_models/'+model, 'lab_'+str(subj)+'.pkl')
            proba = clf.predict_proba(fs[subj][sess][:, 1:])
            proba = np.array(proba)
            proba = proba[:, 1]
            
            gt = np.copy(ssilv[subj][sess][:, 3])
            ssilv[subj][sess][:, 3] = proba                    
            ba[subj][sess] = ssilv[subj][sess][:, 2:]
            print("Proba shape, pos, gtpos:", proba.shape, np.sum(proba>=0.5), np.sum(gt==1)/40)

    mfileu.write_file('all_proba', 'all_proba_bite_lab_{}.pkl'.format(model), ba)        

