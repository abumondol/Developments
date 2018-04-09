
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os


# In[2]:


def create_directory(path):
    if not os.path.exists(path):
        print('Creating directory: ', path)
        os.makedirs(path)


# In[3]:


def process_data(lines, offset, annots):
    #print(annots)    
    tokens = lines[0].split(",")
    if len(tokens)==1:
        lines = lines[1:]
    
    data = []
    for line in lines:
        tokens = line.split(",")
        sensor_id = int(tokens[1])
        if sensor_id in [1, 4, 9, 11]:
            t = int(tokens[0])
            x = float(tokens[3])
            y = float(tokens[4])
            z = float(tokens[5])
            data.append([t, sensor_id, x, y, z])
        
    data = np.array(data)  
    data[:, 0] = data[:, 0] - data[0, 0]
    data[:, 0] = data[:, 0]/1e9
        
    if len(annots)==0:                               
        res_annots = np.array([])        
    else:
        annots.sort(axis=0)
        annots = annots[annots[:, 1]<1000, :]
        annots[:, 0] = annots[:, 0] - offset
                    
    return data, annots


# In[4]:


dest_folder = 'C:/ASM/DevData/eating/datasets'
create_directory(dest_folder)


# In[5]:


subject_serial = 0
all_data = []


# In[6]:


#read data usc
srcFolder = 'D:/Box Sync/MyData/Eating m2fed/usc_meals';
subjects = [
    ['0102', 94.6],
    ['0103', 67.5],
    ['0301', 59.8],
    ['0601', 26.1],
    ['0602', 16.0],
    ['0603', 17.1],
    ['0801', 82.0],
    ['0803', 67.9],
    ['0901', 63.9],
    ['0902', 123.2],
    ['1001', 89.5],    
    ['1303', 123.8],
    ['1304', 107.1],
    ['1305', 135.5]]


for subj in range(len(subjects)):
    subject_id = subjects[subj][0]
    print(subject_id)
    file_path = srcFolder+'/sensor_data/'+subject_id+'_right.wada'
    with open(file_path) as file:
        raw_data = file.readlines()

    file_path = srcFolder+'/annotations/processed/'+ subject_id+ '_annotations_right.csv'
    annots = np.genfromtxt(file_path, delimiter=",")
    
    offset = subjects[subj][1]

    data, annots = process_data(raw_data, offset, annots)
    if subj ==0:             
        data = data[data[:,0]<42*60, :]        
    
    #np.savetxt(dest_file_path+'data_'+str(file_serial), data, delimiter=",")
    #np.savetxt(dest_file_path+'annots_'+str(file_serial), annots, delimiter=",")
    
    d = {'data':data, 'annots':annots, 'type':'usc'}
    all_data.append([d])    
   


# In[7]:


#read data uva
session_counts = [2, 4, 2, 2, 4, 5, 1]
srcFolder = 'D:/Box Sync/MyData/Eating m2fed/uva_meals'

file_path = srcFolder +'/offsets.csv'
all_offsets = []
with open(file_path) as file:
    lines = file.readlines()
    for line in lines:       
        tokens = line.split(",")
        ofs = []
        for t in tokens:
            t = t.rstrip()            
            if len(t)>0:
                t= float(t)
                ofs.append(t)
        all_offsets.append(ofs)
        
print(all_offsets)

for subj in range(7):
    sess_count = session_counts[subj]
    subject_data = []
    for sess in range(sess_count):
        print('Processing ', (subj+1), (sess+1))
        file_path = srcFolder+'/subject_'+ str(subj+1)+ '/subject'+ str(subj+1)+ '_right_session'+ str(sess+1)+'.wada'
        with open(file_path) as file:
            raw_data = file.readlines()
        
        file_path = srcFolder + '/annotations/processed/subject'+ str(subj+1)+ '_annotations_right_session'+ str(sess+1) + '.csv'
        annots = np.genfromtxt(file_path, delimiter=",")
        
        offset = all_offsets[subj][sess]
        
        data, annots = process_data(raw_data, offset, annots)
        #np.savetxt(dest_file_path+'data_'+str(file_serial), data, delimiter=",")
        #np.savetxt(dest_file_path+'annots_'+str(file_serial), annots, delimiter=",")
        d = {'data':data, 'annots':annots, 'type':'uva'}
        subject_data.append(d)
        
    all_data.append(subject_data)
        


# In[8]:


with open(dest_folder+'/our_lab_dataset.pkl', 'wb') as file:
    pickle.dump(all_data, file)


# In[9]:


print("total subjects in lab: ", len(all_data))
for s in range(len(all_data)):
    print('Subject {}: Session count {}'.format(s, len(all_data[s])))
    


# In[10]:


srcFolder = 'D:/Box Sync/MyData/Eating m2fed/uva_noneat_home/'
subjects = ['abu','emi','sarah','meiyi']

all_data = []
for subj in subjects:    
    folder_path = srcFolder + subj + '_home_noneat'
    files = os.listdir(folder_path);
    for f in files:
        file_path = folder_path+ '/' + f
        print(file_path)
        with open(file_path) as file:
            raw_data = file.readlines()
        
        annots = [];        
        offset = 0;        
       
        data, annots = process_data(raw_data, offset, annots)
        #np.savetxt(dest_file_path+'accel_'+str(file_serial), accel, delimiter=",")
        d = {'data':data, 'annots':annots, 'subject_name':subj}
        all_data.append(d)        


# In[11]:


with open(dest_folder+'/our_free_dataset.pkl', 'wb') as file:
    pickle.dump(all_data, file)

