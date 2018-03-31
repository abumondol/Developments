
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import matplotlib.pyplot as plt
import data_process_utils as dpu
get_ipython().run_line_magic('matplotlib', 'inline')


# In[2]:


def process_annots(annots, min_bite_distance):    
    count = len(annots)
    
    for i in range(count):
        if 1 <= annots[i, 1] < 400:
            annots[i, 1] = 1
        elif 400 <= annots[i, 1] < 1000:
            annots[i, 1] = 2
        else:
            annots[i, 1] = 0
    
    annots = annots[annots[:, 1]>0, :]
    count = len(annots)
    flags = np.ones((count, ))
    
    for i in range(1, count):
        if annots[i, 0] - annots[i-1, 0]<=min_bite_distance:
            flags[i-1] = 0
            
    annots = annots[flags==1, :]    
    return annots
    


# In[3]:


with open('C:/ASM/DevData/eating/datasets/our_lab_dataset.pkl', 'rb') as file:
    data = pickle.load(file)
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
        
        accel = dpu.resample(accel, sampling_rate, [start_time, end_time])
        gyro = dpu.resample(gyro, sampling_rate, [start_time, end_time])
        grav = dpu.resample(grav, sampling_rate, [start_time, end_time])
        grav = grav[:, 2:]        
        mag = np.sqrt(np.sum(grav*grav, axis=1, keepdims=True))
        mag = np.concatenate((mag, mag, mag), axis=1)        
        grav = grav/mag
        
        
        t = accel[:, 0].reshape((-1, 1))
        offset = t[0, 0]        
        t = t-offset  # starting from time 0
        print('Offset: ', offset)
        
        d = np.concatenate((t, grav, accel[:, 2:], gyro[:, 2:]), axis =1)
        
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
        
        a[:, 0] = dpu.time_to_index(d, a[:, 0])        
        
        data[subject][sess]['data'] = d
        data[subject][sess]['annots'] = a.astype(int)


# In[4]:


with open('C:/ASM/DevData/eating/data/uva_lab_data.pkl', 'wb') as file:
    pickle.dump(data, file)

