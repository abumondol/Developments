
# coding: utf-8

# In[ ]:


import numpy as np
import pickle
import matplotlib.pyplot as plt
import data_process_utils as dpu
get_ipython().run_line_magic('matplotlib', 'inline')


# In[ ]:


with open('C:/ASM/DevData/eating/datasets/our_lab_data.pkl', 'rb') as file:
    data = pickle.load(file)
print("Total subjects: ", len(data))


# In[ ]:


for subject in range(len(data)):
    for sess in range(len(data[subject])):
        print('\nSubject, Sess >> ', subject, sess)
        d = data[subject][sess]['data']
        a = data[subject][sess]['annots']
        
        #if subject == 2:
        #    print(len(d), len(a))
        #    d = d[d[:,0]>=1250, :]
        #    a = a[a[:,0]>=1250, :]
        #    data[subject][sess]['data'] = d
        #    data[subject][sess]['annots'] = a            
        #print(len(d), len(a))
        
        
        accel = d[d[:, 1]==1, :]
        gyro = d[d[:, 1]==4, :]
        grav = d[d[:, 1]==9, :]
        #quat = d[d[:, 1]==11, :]        
        
        mx = max([accel[0,0], gyro[0,0], grav[0,0], quat[0,0]])
        mn = min([accel[-1,0], gyro[-1,0], grav[-1,0], quat[-1, 0]])
        
        #accel = accel[(accel[:,0]>=mx) & (accel[:,0]<=mn), :]
        #gyro = gyro[(gyro[:,0]>=mx) & (gyro[:,0]<=mn), :]
        #gyro = gyro[(gyro[:,0]>=mx) & (gyro[:,0]<=mn), :]        
        
        print('  Start times: accel, gyro, grav, quat >> ', accel[0,0], gyro[0, 0], grav[0,0], quat[0,0])
        print('  Start times: accel, gyro, grav, quat >> ', accel[-1,0], gyro[-1, 0], grav[-1,0], quat[-1, 0])
        print('  Sample count: accel, gyro, grav, quat >> ', len(accel), len(gyro), len(grav), len(quat))        
        


# In[ ]:


get_ipython().run_line_magic('load_ext', 'autoreload')
get_ipython().run_line_magic('autoreload', '2')


# In[ ]:


sid = 1
for subject in range(sid, sid+1):
    for sess in range(len(data[subject])):        
        ds = data[subject][sess]['data']
        a = data[subject][sess]['annots']
        
        
        accel = ds[ds[:, 1]==1, :]
        gyro = ds[ds[:, 1]==4, :]
        grav = ds[ds[:, 1]==9, :]
        quat = ds[ds[:, 1]==11, :]
        
        print(grav.shape)
        grav = dpu.resample(grav, 16)
        print(grav.shape)
                
        indices = dpu.time_to_index(grav, a[:, 0])
        indices = indices.astype(int)        
        for i in range(10):
            ix = indices[i]
            t = a[i, 0]
            
            fig = plt.figure(figsize=(14, 7))
            subplot = fig.add_subplot(111)        
            
            
            d = grav[(grav[:,0]>=t-5) & (grav[:,0]<=t+5), :]
            subplot.plot(d[:, 0], d[:, 2], label='GravX', linestyle='-', color='red')
            subplot.plot(d[:, 0], d[:, 3], label='GravY', linestyle='-', color='green')
            subplot.plot(d[:, 0], d[:, 4], label='GravZ', linestyle='-', color='blue')
            
            '''
            d = accel[(accel[:,0]>=t-5) & (accel[:,0]<=t+5), :]
            subplot.plot(d[:, 0], d[:, 2], label='AceelX', linestyle='--', color='red')
            subplot.plot(d[:, 0], d[:, 3], label='AceelY', linestyle='--', color='green')
            subplot.plot(d[:, 0], d[:, 4], label='AceelZ', linestyle='--', color='blue')
            
            
            d = quat[(quat[:,0]>=t-5) & (quat[:,0]<=t+5), :]
            _, _, Rz = dpu.quat2mat(d[:, 2:])
            Rz = Rz*9.8
            subplot.plot(d[:, 0], Rz[:, 0], label='QuatX', linestyle=':', color='red')
            subplot.plot(d[:, 0], Rz[:, 1], label='QuatY', linestyle=':', color='green')
            subplot.plot(d[:, 0], Rz[:, 2], label='QuatZ', linestyle=':', color='blue')
            '''
            
            
            subplot.legend()            
            plt.title('Subject {}, Sess {}, Annot Index {}'.format(subject, sess, i))            
            plt.show()                        

