
# coding: utf-8

# In[1]:


import numpy as np
import shutil
import gzip
import os


# In[5]:


src_folder = "C:/ASM/PublicData/extrasensory/ExtraSensory.per_uuid_features_labels"
dest_folder = "C:/ASM/PublicData/extrasensory/per_uuid_features_labels_extracted"

if not os.path.exists(dest_folder):
    os.makedirs(dest_folder)

file_list = os.listdir(src_folder)
print("total files: ", len(file_list))
#print(file_list)

for file in file_list:
    with gzip.open(src_folder + '/' + file, 'rb') as f_in:
        file = file.replace(".gz", "")        
        with open(dest_folder + '/'+ file, 'wb') as f_out:
            shutil.copyfileobj(f_in, f_out)
        


# In[2]:


src_folder = "C:/ASM/PublicData/extrasensory/ExtraSensory.per_uuid_original_labels"
dest_folder = "C:/ASM/PublicData/extrasensory/per_uuid_original_labels_extracted"

if not os.path.exists(dest_folder):
    os.makedirs(dest_folder)

file_list = os.listdir(src_folder)
print("total files: ", len(file_list))
#print(file_list)

for file in file_list:
    with gzip.open(src_folder + '/' + file, 'rb') as f_in:
        file = file.replace(".gz", "")        
        with open(dest_folder + '/'+ file, 'wb') as f_out:
            shutil.copyfileobj(f_in, f_out)
        

