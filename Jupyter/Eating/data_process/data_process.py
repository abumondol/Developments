
# coding: utf-8

# In[2]:


import numpy as np
import os
import sys


# In[3]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_steven_lab_utils as mslabu
import my_steven_free_utils as msfreeu


# In[6]:


#Normalize data
lab_free='lab'
ds = mfileu.read_file('data', lab_free+'_data_steven_right_smoothed.pkl')
for subj in range(len(ds)):
    for sess in range(len(ds[subj])):
        print(subj, sess)
        d = ds[subj][sess]
        a = d[:, 1:4]
        g = d[:, 4:7]
        #m = np.sqrt(np.sum(a*a, axis=1)).reshape((-1, 1))
        #m = np.concatenate((m, m, m), axis=1)
        #a = a/m
        a = np.clip(a, -9.8, 9.8)        
        a = (a+9.8)/(2*9.8)
        g = np.clip(g, -15, 15)        
        g = (g+15)/(2*15)
                
        d[:, 1:4] = a
        d[:, 4:7] = g
        ds[subj][sess] = d
        
mfileu.write_file('data', lab_free+'_data_steven_right_smoothed_accel_gyro_normalized.pkl', ds)


# In[7]:


ds = mfileu.read_file('data', lab_free+'_data_steven_right_smoothed_accel_gyro_normalized.pkl')
for subj in range(len(ds)):
    for sess in range(len(ds[subj])):
        print(subj, sess)
        d = ds[subj][sess]
        a = d[:, 1:4]
        g = d[:, 4:7]
        assert np.sum((a>1) | (a<0)) ==0
        assert np.sum((g>1) | (g<0)) ==0


# # Separate Lab data Steven
# ds = mfileu.read_file('X_data', 'lab_data_steven.pkl')
# ds, _, _ = mslabu.separate_right_left_annots(ds)
# 
# mfileu.write_file('data', 'lab_data_steven_right.pkl', ds)

# # Separate Lab data Steven smoothed
# ds = mfileu.read_file('X_data', 'lab_data_steven_smoothed.pkl')
# ds, _, annots = mslabu.separate_right_left_annots(ds)
# 
# mfileu.write_file('data', 'lab_data_steven_right_smoothed.pkl', ds)

# # Process Steven Lab Annots for right hand only
# ds = mfileu.read_file('X_data', 'lab_data_steven.pkl')
# _, _, annots = mslabu.separate_right_left_annots(ds)
# 
# for subj in range(len(annots)):
#     for sess in range(len(annots[subj])):
#         a = annots[subj][sess]
#         a = a[a[:, 3]!=2, :] # right hand only
#         annots[subj][sess] = a
# 
# mfileu.write_file('data', 'lab_annots_steven_right.pkl', annots)

# # Sperate UVA lab data  and Annots 
# ds = mfileu.read_file('X_data', 'lab_data_uva.pkl')
# 
# right, annots = [], []
# for subj in range(len(ds)):
#     subj_right, subj_annots = [], []
#     for sess in range(len(ds[subj])):
#         subj_right.append(ds[subj][sess]['data_right'])        
#         subj_annots.append(ds[subj][sess]['annots'])
#         
#     right.append(subj_right)    
#     annots.append(subj_annots)
# 
# mfileu.write_file('data', 'lab_data_uva_right.pkl', right)
# mfileu.write_file('data', 'lab_annots_uva_right.pkl', annots)

# # Sperate UVA lab data Smoothed 
# ds = mfileu.read_file('X_data', 'lab_data_uva_smoothed.pkl')
# 
# right, annots = [], []
# for subj in range(len(ds)):
#     subj_right, subj_annots = [], []
#     for sess in range(len(ds[subj])):
#         subj_right.append(ds[subj][sess]['data_right'])        
#         subj_annots.append(ds[subj][sess]['annots'])
#         
#     right.append(subj_right)    
#     annots.append(subj_annots)
# 
# mfileu.write_file('data', 'lab_data_uva_right_smoothed.pkl', right)
# 

# # Combine Steven and UVA lab data
# stev = mfileu.read_file('data', 'lab_data_steven_right.pkl')
# uva = mfileu.read_file('data', 'lab_data_uva_right.pkl')
# 
# for subj in range(len(uva)):
#     stev.append(uva[subj])
#     
# for subj in range(len(stev)):    
#         print(subj, len(stev[subj]))
#         
# mfileu.write_file('data', 'lab_data_combo_right.pkl', stev)

# # Combine Steven and UVA lab data smoothed
# stev = mfileu.read_file('data', 'lab_data_steven_right_smoothed.pkl')
# uva = mfileu.read_file('data', 'lab_data_uva_right_smoothed.pkl')
# 
# for subj in range(len(uva)):
#     stev.append(uva[subj])
#     
# for subj in range(len(stev)):    
#         print(subj, len(stev[subj]))
#         
# mfileu.write_file('data', 'lab_data_combo_right_smoothed.pkl', stev)

# # Combine Steven and UVA lab Annots
# stev = mfileu.read_file('data', 'lab_annots_steven_right.pkl')
# uva = mfileu.read_file('data', 'lab_annots_uva_right.pkl')
# 
# for subj in range(len(uva)):
#     stev.append(uva[subj])
#     
# for subj in range(len(stev)):    
#         print(subj, len(stev[subj]))
#         
# mfileu.write_file('data', 'lab_annots_combo_right.pkl', stev)

# In[3]:


# Process Annnots Steven Free, Remove Annots out of data boundary
ds = mfileu.read_file('X_data', 'free_data_steven_right.pkl')
annots = mfileu.read_file('X_data', 'free_data_steven_annots.pkl')

for subj in range(len(ds)):
    for sess in range(len(ds[subj])):
        a = annots[subj][sess]
        dcount = len(ds[subj][sess])
        
        annots[subj][sess] = msfreeu.process_anntos(dcount, a)
        assert annots[subj][sess][-1, 1] <= dcount
        
mfileu.write_file('data', 'free_annots_steven_processed.pkl', annots)

