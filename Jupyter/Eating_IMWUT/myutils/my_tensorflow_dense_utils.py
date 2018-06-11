
# coding: utf-8

# In[1]:


import numpy as np
import tensorflow as tf


# In[2]:


def fc_layer(x, size_out, activation=None, name="FC_Layer" ):
    dims = x.get_shape().as_list()
    size_in = dims[-1]
    with tf.name_scope(name):
        W = tf.Variable(tf.truncated_normal([size_in, size_out], stddev=0.1), name="W")
        b = tf.Variable(tf.constant(0.0, shape=[size_out]), name="b")
        output = tf.matmul(x, W) + b
        
        assert activation in [None, "relu", "tanh", "sigmoid"]            
        
        if activation=="relu":
            print("Relu activation")
            output = tf.nn.relu(output)
        elif activation=="tanh":
            print("Tanh activation")
            output = tf.nn.tanh(output)
        elif activation=="sigmoid":
            print("Sigmoid activation")
            output = tf.nn.sigmoid(output)        
            
        tf.summary.histogram('weights', W)
        tf.summary.histogram('biases', b)
        tf.summary.histogram('outputs', output)
        return output
    


# In[ ]:


'''
fc_1 = fc_layer(combo_flattened, combo_shape[-1], 256, name='FC_1', relu=True)
fcdrop_1 = tf.nn.dropout(fc_1, keep_prob, name='fcdrop_1')    
fc_2 = fc_layer(fcdrop_1, 256, 128, name='FC_2', relu=True)
fcdrop_2 = tf.nn.dropout(fc_2, keep_prob, name='fcdrop_2') 
#fc_3 = fc_layer(fcdrop_2, 512, 512, name='FC_3', relu=True)
#fcdrop_3 = tf.nn.dropout(fc_3, keep_prob, name='fcdrop_3') 
logits = fc_layer(fcdrop_2, 128, y_shape[-1], name='FC_Final')
'''

