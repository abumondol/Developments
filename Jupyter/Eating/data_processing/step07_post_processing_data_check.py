
# coding: utf-8

# In[13]:


import numpy as np
import pickle
import sys
import matplotlib.pyplot as plt
import data_process_utils as dpu
get_ipython().run_line_magic('matplotlib', 'inline')


# In[14]:


with open('C:/ASM/DevData/eating/data/uva_lab_data.pkl', 'rb') as file:
    data = pickle.load(file)


# In[15]:


#print(len(data))
subject, sess = 15,3
print(len(data[subject]))
d = data[subject][sess]['data']
a = data[subject][sess]['annots']
print(d.shape, a.shape)
#print(a)
print(d[100:110, :])


#t = d[a[:, 0], 0]
#print(t)
#t = d[a[:, 1], 0]
#print(t)
#print(a)


# In[10]:


s, e = 3000, 7000

fig = plt.figure(figsize=(14, 7))
subplot = fig.add_subplot(111)        

mag = np.sqrt(np.sum(d[s:e, 1:4]*d[s:e, 1:4], axis=1))
subplot.plot(d[s:e, 0], d[s:e, 1], label='GravX', linestyle='-', color='red')
subplot.plot(d[s:e, 0], d[s:e, 2], label='GravY', linestyle='-', color='green')
subplot.plot(d[s:e, 0], d[s:e, 3], label='GravZ', linestyle='-', color='blue')
subplot.plot(d[s:e, 0], mag, label='GravMag', linestyle='--', color='black')

subplot.legend()            
plt.title('Gravity')            
plt.show()         



fig = plt.figure(figsize=(14, 7))
subplot = fig.add_subplot(111)        

mag = np.sqrt(np.sum(d[s:e, 4:7]*d[s:e, 4:7], axis=1))
subplot.plot(d[s:e, 0], d[s:e, 4], label='AccelX', linestyle='-', color='red')
subplot.plot(d[s:e, 0], d[s:e, 5], label='AccelY', linestyle='-', color='green')
subplot.plot(d[s:e, 0], d[s:e, 6], label='AccelZ', linestyle='-', color='blue')
subplot.plot(d[s:e, 0], mag, label='AccelMag', linestyle='--', color='black')

subplot.legend()            
plt.title('Accel')            
plt.show()         


fig = plt.figure(figsize=(14, 7))
subplot = fig.add_subplot(111)        

mag = np.sqrt(np.sum(d[s:e, 7:]*d[s:e, 7:], axis=1))
subplot.plot(d[s:e, 0], d[s:e, 7], label='GyroX', linestyle='-', color='red')
subplot.plot(d[s:e, 0], d[s:e, 8], label='GyroY', linestyle='-', color='green')
subplot.plot(d[s:e, 0], d[s:e, 9], label='GyroZ', linestyle='-', color='blue')
subplot.plot(d[s:e, 0], mag, label='GyroMag', linestyle='--', color='black')

subplot.legend()            
plt.title('Gyro')            
plt.show() 



