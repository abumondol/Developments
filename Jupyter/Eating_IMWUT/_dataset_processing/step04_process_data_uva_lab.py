
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfu
import my_data_process_utils as mdpu


# In[3]:


def process_annots(annots, min_bite_distance):    
    count = len(annots)
    a = np.zeros((count, 1))
    
    for i in range(count):
        if 1 <= annots[i, 1] < 400:
            a[i, 0] = 1
        elif 400 <= annots[i, 1] < 1000:
            a[i, 0] = 2
        else:
            a[i, 0] = 0
    
    annots = np.concatenate((annots, a), axis=1)
    annots = annots[a[:, 0]>0, :]
    count = len(annots)
    flags = np.ones((count, ))
    
    for i in range(1, count):
        if annots[i, 0] - annots[i-1, 0]<=min_bite_distance:
            flags[i-1] = 0
            
    annots = annots[flags==1, :]    
    return annots
    


# In[4]:


data = mfu.read_file('datasets', 'our_lab_dataset.pkl')
print("Total subjects: ", len(data))

sampling_rate = 16
min_bite_distance = 1
for subject in range(len(data)):
    for sess in range(len(data[subject])):
        print('\nSubject, Sess >> ', subject, sess)
        d = data[subject][sess]['data']
        a = data[subject][sess]['annots']
        a = process_annots(a, min_bite_distance)
        
        accel = d[d[:, 1]==1, :]
        gyro = d[d[:, 1]==4, :]
        grav = d[d[:, 1]==9, :]        
        
        start_time = max([accel[0,0], gyro[0,0], grav[0,0]])
        end_time = min([accel[-1,0], gyro[-1,0], grav[-1,0]])
        
        accel = mdpu.resample(accel, sampling_rate, [start_time, end_time])
        gyro = mdpu.resample(gyro, sampling_rate, [start_time, end_time])
        grav = mdpu.resample(grav, sampling_rate, [start_time, end_time])
        
        grav = grav[:, 2:]        
        linaccel = accel[:, 2:] - grav        
        m = np.sqrt(np.sum(grav*grav, axis=1, keepdims=True))        
        grav = grav/m
        
        
        t = accel[:, 0].reshape((-1, 1))
        offset = t[0, 0]        
        t = t-offset  # starting from time 0
        print('Offset: ', offset)
        
        d = np.concatenate((t, accel[:, 2:], gyro[:, 2:], linaccel, grav), axis =1)
        
        a[:, 0] = a[:, 0] - offset  #offset the annots also
        
        if a[0,0]<=0:
            a = a[a[:,0]>0, :]
            print("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
            print("First bite negative ", a[0,0])
            print("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
        
        if a[0,0]<10:
            print("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
            print("First bite with in 10 second ", a[0,0])
            print("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
        
        a[:, 0] = a[:, 0]*sampling_rate
        
        data[subject][sess]['data_right'] = d
        data[subject][sess]['annots'] = a.astype(int)


# In[5]:


mfu.write_file('data', 'lab_data_uva.pkl', data)

