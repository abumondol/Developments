
# coding: utf-8

# In[1]:


import tensorflow as tf


# In[2]:


def conv_layer(x, size_in, size_out, ksize, strides, padding, name):    
    strides = [1, strides[0], strides[1], 1]    
    with tf.name_scope(name):
        W = tf.Variable(tf.truncated_normal([ksize[0], ksize[1], size_in, size_out], stddev=0.1), name="W")
        b = tf.Variable(tf.constant(0.1, shape=[size_out]), name="b")
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
        output = tf.nn.max_pool(x, ksize, strides, padding)
        tf.summary.histogram("outputs", output)
        return output
    
def fc_layer(x, size_in, size_out, name, relu=False):
    with tf.name_scope(name):
        W = tf.Variable(tf.truncated_normal([size_in, size_out], stddev=0.1), name="W")
        b = tf.Variable(tf.constant(0.1, shape=[size_out]), name="b")
        output = tf.matmul(x, W) + b
        if relu:
            output = tf.nn.relu(output)
            
        tf.summary.histogram('weights', W)
        tf.summary.histogram('biases', b)
        tf.summary.histogram('outputs', output)
        return output    

