
# coding: utf-8

# In[6]:


import numpy as np
import tensorflow as tf


# In[6]:


def conv_layer(x, size_in, size_out, ksize, strides, padding, name):    
    strides = [1, strides[0], strides[1], 1]   
    with tf.name_scope(name):
        W = tf.Variable(tf.truncated_normal([ksize[0], ksize[1], size_in, size_out], stddev=0.1), name="W")
        b = tf.Variable(tf.constant(0.0, shape=[size_out]), name="b")
        conv = tf.nn.conv2d(x, W, strides=strides, padding=padding)
        output = tf.nn.relu(conv + b)
        tf.summary.histogram("weights", W)
        tf.summary.histogram("biases", b)
        tf.summary.histogram("outputs", output)
        return output
    
def maxpool_layer(x, ksize, strides, padding, name):
    ksize = [1, ksize[0], ksize[1], 1]
    strides = [1, strides[0], strides[1], 1]
    with tf.name_scope(name):
        output = tf.nn.max_pool(x, ksize=ksize, strides=strides, padding=padding)
        tf.summary.histogram("outputs", output)
        return output


# In[ ]:


def gravity_conv_net(x, name):
    x_shape =x.get_shape().as_list()
    print('Inside one_3dsensor_conv_net: ',name,', x_shape', x_shape)
    
    axis_count = x_shape[-1]
    print("  Axis count: ", axis_count)
    with tf.name_scope(name):
        x = tf.reshape(x, shape=[-1, x.shape[1], x.shape[2], 1], name="reshape")
        conv_1 = conv_layer(x, size_in=1, size_out=64, ksize=[3,3], strides=[1,1], padding="VALID", name='conv_1')        
        conv_2 = conv_layer(conv_1, size_in=64, size_out=64, ksize=[3,1], strides=[1,1], padding="VALID", name='conv_2')
        maxpool_1 = maxpool_layer(conv_2, ksize=[3,1], strides=[2,1], padding="VALID", name="maxpool_1")
        print("  Conv_1, maxpool_1 shape: ", conv_1.get_shape().as_list(), maxpool_1.get_shape().as_list())

        conv_3 = conv_layer(maxpool_1, size_in=64, size_out=64, ksize=[3,1], strides=[1,1], padding="VALID", name='conv_3')        
        conv_4 = conv_layer(conv_3, size_in=64, size_out=64, ksize=[3,1], strides=[1,1], padding="VALID", name='conv_4')
        maxpool_2 = maxpool_layer(conv_4, ksize=[3,1], strides=[2,1], padding="VALID", name="maxpool_2")
        print("  Conv_3, maxpool_2 shape: ", conv_3.get_shape().as_list(), maxpool_2.get_shape().as_list())
        
        sz = maxpool_2.get_shape().as_list()
        flattened = tf.reshape(maxpool_2, shape=[-1, sz[1], sz[2]*sz[3]], name="Flattened")
        return flattened


# In[7]:


def one_3dsensor_conv_net(x, name):
    x_shape =x.get_shape().as_list()
    print('Inside one_3dsensor_conv_net: ',name,', x_shape', x_shape)
    
    axis_count = x_shape[-1]
    print("  Axis count: ", axis_count)
    with tf.name_scope(name):
        x = tf.reshape(x, shape=[-1, x.shape[1], x.shape[2], 1], name="reshape")
        conv_1 = conv_layer(x, size_in=1, size_out=64, ksize=[3,3], strides=[1,1], padding="VALID", name='conv_1')        
        #maxpool_1 = maxpool_layer(conv_1, ksize=[3,1], strides=[2,1], padding="VALID", name="maxpool_1")
        #print("  Conv_1, maxpool_1 shape: ", conv_1.get_shape().as_list(), maxpool_1.get_shape().as_list())

        conv_2 = conv_layer(conv_1, size_in=64, size_out=64, ksize=[3,1], strides=[1,1], padding="VALID", name='conv_2')
        maxpool_2 = maxpool_layer(conv_2, ksize=[3,1], strides=[2,1], padding="VALID", name="maxpool_2")
        print("  Conv_2, maxpool_2 shape: ", conv_2.get_shape().as_list(), maxpool_2.get_shape().as_list())
        
        conv_3 = conv_layer(maxpool_2, size_in=64, size_out=64, ksize=[3,1], strides=[1,1], padding="VALID", name='conv_2')
        #maxpool_3 = maxpool_layer(conv_3, ksize=[3,1], strides=[2,1], padding="VALID", name="maxpool_2")
        #print("  Conv_3, maxpool_3 shape: ", conv_3.get_shape().as_list(), maxpool_2.get_shape().as_list())
        
        conv_4 = conv_layer(conv_3, size_in=64, size_out=64, ksize=[3,1], strides=[1,1], padding="VALID", name='conv_2')
        maxpool_4 = maxpool_layer(conv_3, ksize=[3,1], strides=[2,1], padding="VALID", name="maxpool_2")
        print("  Conv_4, maxpool_4 shape: ", conv_4.get_shape().as_list(), maxpool_4.get_shape().as_list())
        
        return maxpool_4


# In[8]:


def combine_sensors_conv_net(x, name):
    x_shape =x.get_shape().as_list()
    print('Inside combine_sensor_conv_net: ',name,', x_shape', x_shape)
    
    axis_count = x_shape[2]
    size_in = x_shape[-1]
    print("  Axis count: ", axis_count)
    with tf.name_scope(name):        
        conv_1 = conv_layer(x, size_in=size_in, size_out=64, ksize=[3,axis_count], strides=[1,1], padding="VALID", name='conv_1')        
        #maxpool_1 = maxpool_layer(conv_1, ksize=[3,1], strides=[2,1], padding="VALID", name="maxpool_1")
        #print("  Conv_1, maxpool_1 shape: ", conv_1.get_shape().as_list(), maxpool_1.get_shape().as_list())

        conv_2 = conv_layer(conv_1, size_in=64, size_out=64, ksize=[3,1], strides=[1,1], padding="VALID", name='conv_2')
        maxpool_2 = maxpool_layer(conv_2, ksize=[3,1], strides=[2,1], padding="VALID", name="maxpool_2")
        print("  Conv_2, maxpool_2 shape: ", conv_2.get_shape().as_list(), maxpool_2.get_shape().as_list())
        
        sz = maxpool_2.get_shape().as_list()
        flattened = tf.reshape(maxpool_2, shape=[-1, sz[1], sz[2]*sz[3]], name="Flattened")
        return flattened


# In[9]:


def all_sensor_net(x, name):
    x_shape = x.get_shape().as_list()    
    print('Inside all_sensor_net: x_shape:', x_shape)    
    
    with tf.name_scope(name):
        all_sensors = []        
        for i in range(0, x_shape[-1], 3):
            sensor_data = x[:, :, i:i+3]            
            a = one_3dsensor_conv_net(sensor_data, name="one_3dsensor_conv_net_"+str(i//3))
            all_sensors.append(a)
            print("One 3d Sensor flattened shape: ", i, all_sensors[-1].get_shape().as_list())
        
        print("All sensor list size:", len(all_sensors))        
        #combo_flattened = tf.concat(all_sensors, axis=1, name='concat_sensors') # 
        combo_joined = tf.concat(all_sensors, axis=2, name='concat_sensors') # 
        combo_shape = combo_joined.get_shape().as_list()
        print("Combo shape all sensor net: ", combo_shape)
        
        flattened = combine_sensors_conv_net(combo_joined, name="combined_sensors_conv_net")
        print("Final Conv net output shape: ", flattened.get_shape().as_list())
        return flattened


# In[ ]:


##################################### The followings are for bite ##############################


# In[1]:


def one_3dsensor_conv_net_bite(x, name):
    x_shape =x.get_shape().as_list()
    print('Inside one_3dsensor_conv_net: ',name,', x_shape', x_shape)
    
    axis_count = x_shape[-1]
    print("  Axis count: ", axis_count)
    with tf.name_scope(name):
        x = tf.reshape(x, shape=[-1, x.shape[1], x.shape[2], 1], name="reshape")
        conv_1 = conv_layer(x, size_in=1, size_out=32, ksize=[3,3], strides=[1,1], padding="VALID", name='conv_1')        
        #maxpool_1 = maxpool_layer(conv_1, ksize=[3,1], strides=[2,1], padding="VALID", name="maxpool_1")
        #print("  Conv_1, maxpool_1 shape: ", conv_1.get_shape().as_list(), maxpool_1.get_shape().as_list())

        conv_2 = conv_layer(conv_1, size_in=32, size_out=32, ksize=[3,1], strides=[1,1], padding="VALID", name='conv_2')
        maxpool_2 = maxpool_layer(conv_2, ksize=[3,1], strides=[2,1], padding="VALID", name="maxpool_2")
        print("  Conv_2, maxpool_2 shape: ", conv_2.get_shape().as_list(), maxpool_2.get_shape().as_list())
        
        return maxpool_2


# In[ ]:


def combine_sensors_conv_net_bite(x, name):
    x_shape =x.get_shape().as_list()
    print('Inside combine_sensor_conv_net: ',name,', x_shape', x_shape)
    
    axis_count = x_shape[2]
    size_in = x_shape[-1]
    print("  Axis count: ", axis_count)
    with tf.name_scope(name):        
        conv_1 = conv_layer(x, size_in=size_in, size_out=32, ksize=[3,axis_count], strides=[1,1], padding="VALID", name='conv_1')        
        #maxpool_1 = maxpool_layer(conv_1, ksize=[3,1], strides=[2,1], padding="VALID", name="maxpool_1")
        #print("  Conv_1, maxpool_1 shape: ", conv_1.get_shape().as_list(), maxpool_1.get_shape().as_list())

        conv_2 = conv_layer(conv_1, size_in=32, size_out=32, ksize=[3,1], strides=[1,1], padding="VALID", name='conv_2')
        maxpool_2 = maxpool_layer(conv_2, ksize=[3,1], strides=[2,1], padding="VALID", name="maxpool_2")
        print("  Conv_2, maxpool_2 shape: ", conv_2.get_shape().as_list(), maxpool_2.get_shape().as_list())
        
        sz = maxpool_2.get_shape().as_list()
        flattened = tf.reshape(maxpool_2, shape=[-1, sz[1], sz[2]*sz[3]], name="Flattened")
        return flattened


# In[ ]:


def all_sensor_net_bite(x, name):
    x_shape = x.get_shape().as_list()    
    print('Inside all_sensor_net: x_shape:', x_shape)    
    
    with tf.name_scope(name):
        all_sensors = []        
        for i in range(0, x_shape[-1], 3):
            sensor_data = x[:, :, i:i+3]            
            a = one_3dsensor_conv_net_bite(sensor_data, name="one_3dsensor_conv_net_"+str(i//3))
            all_sensors.append(a)
            print("One 3d Sensor flattened shape: ", i, all_sensors[-1].get_shape().as_list())
        
        print("All sensor list size:", len(all_sensors))        
        #combo_flattened = tf.concat(all_sensors, axis=1, name='concat_sensors') # 
        combo_joined = tf.concat(all_sensors, axis=2, name='concat_sensors') # 
        combo_shape = combo_joined.get_shape().as_list()
        print("Combo shape all sensor net: ", combo_shape)
        
        flattened = combine_sensors_conv_net_bite(combo_joined, name="combined_sensors_conv_net")
        print("Final Conv net output shape: ", flattened.get_shape().as_list())
        return flattened


# In[ ]:


###################### Previous CNN codes below #########################


# def one_axis_conv_net(x, name):
#     x_shape =x.get_shape().as_list()
#     print('Inside one_axis_net: ',name,', x_shape', x_shape)
#     
#     axis_count = x_shape[-1]
#     print("  Axis count: ", axis_count)
#     with tf.name_scope(name):
#         x = tf.reshape(x, shape=[-1, x.shape[1], x.shape[2], 1], name="reshape")
#         conv_1 = conv_layer(x, size_in=1, size_out=32, ksize=[3,2], strides=[1,1], padding="VALID", name='conv_1')        
#         maxpool_1 = maxpool_layer(conv_1, ksize=[4,1], strides=[4,1], padding="VALID", name="maxpool_1")
#         print("  Conv_1, maxpool_1 shape: ", conv_1.get_shape().as_list(), maxpool_1.get_shape().as_list())
# 
#         conv_2 = conv_layer(maxpool_1, size_in=32, size_out=64, ksize=[3,2], strides=[1,1], padding="VALID", name='conv_2')
#         maxpool_2 = maxpool_layer(conv_2, ksize=[4,1], strides=[4,1], padding="VALID", name="maxpool_2")
#         print("  Conv_2, maxpool_2 shape: ", conv_2.get_shape().as_list(), maxpool_2.get_shape().as_list())
#         
#         #conv_3 = conv_layer(maxpool_2, size_in=64, size_out=128, ksize=[5,1], strides=[1,1], padding="VALID", name='conv_3')
#         #maxpool_3 = maxpool_layer(conv_3, ksize=[2,1], strides=[2,1], padding="VALID", name="maxpool_3")
#         #print("  Conv_3, maxpool_3 shape: ", conv_3.get_shape().as_list(), maxpool_3.get_shape().as_list())
#         
#         sz = maxpool_2.get_shape().as_list()
#         flattened = tf.reshape(maxpool_2, shape=[-1, sz[1]*sz[2]*sz[3]], name="Flattened")
#         return flattened

# def all_axes_net(x, y, keep_prob, name):
#     x_shape = x.get_shape().as_list()
#     y_shape = y.get_shape().as_list()
#     print('Inside all_axes_net: x_shape, y_shape :', x_shape, y_shape)
#     print(type(x_shape))
#     
#     with tf.name_scope(name):
#         all_axes = []
#         
#         '''
#         all_axes = list(range(x_shape[-1]))
#         for i in range(x_shape[-1]):
#             axis_data = x[:, :, i]
#             axis_data = tf.reshape(axis_data, shape=[-1, x.shape[1], 1], name="reshape")
#             #axis_data = tf.slice(x, [0,0,i], [-1, -1, 1], name="slice_"+str(i))
#             all_axes[i] = one_axis_conv_net(axis_data, name="conv_axis_"+str(i))
#             print("Shape ", i, all_axes[i].get_shape().as_list())
#             
#         '''
#         
#         for i in range(0, x_shape[-1], 3):
#             axis_data = x[:, :, i:i+3]            
#             a = one_axis_conv_net(axis_data, name="conv_3_axes_"+str(i))
#             all_axes.append(a)
#             print("Shape ", i, all_axes[-1].get_shape().as_list())
#         
#         print("All axis list size:", len(all_axes))        
#         combo_flattened = tf.concat(all_axes, axis=1, name='concat_axis')
#         combo_shape = combo_flattened.get_shape().as_list()
#         print("Combo shape one net: ", combo_shape)
# 
#         fc_1 = fc_layer(combo_flattened, combo_shape[-1], 256, name='FC_1', relu=True)
#         fcdrop_1 = tf.nn.dropout(fc_1, keep_prob, name='fcdrop_1')    
#         fc_2 = fc_layer(fcdrop_1, 256, 128, name='FC_2', relu=True)
#         fcdrop_2 = tf.nn.dropout(fc_2, keep_prob, name='fcdrop_2') 
#         #fc_3 = fc_layer(fcdrop_2, 512, 512, name='FC_3', relu=True)
#         #fcdrop_3 = tf.nn.dropout(fc_3, keep_prob, name='fcdrop_3') 
#         logits = fc_layer(fcdrop_2, 128, y_shape[-1], name='FC_Final')
#         return logits

# def combo_net(xleft, xmid, xright, y, keep_prob):
#     xmid_shape = xmid.get_shape().as_list()
#     xleft_shape = xleft.get_shape().as_list()
#     xright_shape = xright.get_shape().as_list()
#     y_shape = y.get_shape().as_list()
#     print('Inside combo net(Shapes): xmid, xmid, xright, y :', xmid_shape, xleft_shape, xright_shape, y_shape)
#     
#     fc_left = all_axes_net(xleft, y, keep_prob, name="left")
#     fc_mid = all_axes_net(xmid, y, keep_prob, name="left")
#     fc_right = all_axes_net(xright, y, keep_prob, name="left")
#     
#     combo_flattened = tf.concat([fc_left, fc_mid, fc_right], axis=1, name='concat_combo')
#     combo_shape = combo_flattened.get_shape().as_list()
#     print("Combo shape all net: ", combo_shape)
#     
#     fc_1 = fc_layer(combo_flattened, combo_shape[-1], 512, name='FC_1_combo', relu=True)
#     fcdrop_1 = tf.nn.dropout(fc_1, keep_prob, name='fcdrop_1_combo')    
#     #fc_2 = fc_layer(fcdrop_1, 512, 256, name='FC_2_combo', relu=True)
#     #fcdrop_2 = tf.nn.dropout(fc_2, keep_prob, name='fcdrop_2_combo')
#     logits = fc_layer(fcdrop_1, 512, y_shape[-1], name='FC_Final_combo')
#     
#     return logits    
#     
