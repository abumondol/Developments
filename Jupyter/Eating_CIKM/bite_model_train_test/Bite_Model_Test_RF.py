
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


step = 4
model, lab_free = sys.argv[1], sys.argv[2]
print("Model, lab_free: ", model, lab_free)
assert lab_free in ['lab', 'free']
model_folder = model+'_step'+str(step)


# In[ ]:


fs = mfileu.read_file('features/features_step'+str(step), '{}_features_steven_right.pkl'.format(lab_free))
ssilvg = mfileu.read_file('ssilvg/ssilvg_step'+str(step), '{}_ssilvg_steven_right.pkl'.format(lab_free))
ba = mfileu.read_file('data', '{}_data_steven_blank_array.pkl'.format(lab_free))


# In[ ]:


for subj in range(len(ssilvg)):
    for sess in range(len(ssilvg[subj])):
        print("\nTesting {} subject, sess: {}, {}".format(lab_free, subj, sess))

        if lab_free == 'lab':
            clf = mfileu.read_file('bite_models/'+model_folder, 'lab_'+str(subj)+'.pkl')
        else:            
            if subj<5:
                clf = mfileu.read_file('bite_models/'+model_folder, 'lab_'+str(subj+2)+'.pkl')
            else:
                clf = mfileu.read_file('bite_models/'+model_folder, 'lab_100.pkl')            

        proba = clf.predict_proba(fs[subj][sess][:, 1:])
        proba = np.array(proba)
        proba = proba[:, 1]

        gt = ssilvg[subj][sess][:, 3]
        ssilvg[subj][sess][:, 3] = proba                    
        ba[subj][sess] = ssilvg[subj][sess][:, 2:]            
        print("Proba shape, pos, gtpos:", proba.shape, np.sum(proba>=0.5), np.sum(gt==1))

mfileu.write_file('ipvg/ipvg_step'+str(step), '{}_ipvg_{}.pkl'.format(lab_free, model), ba)

