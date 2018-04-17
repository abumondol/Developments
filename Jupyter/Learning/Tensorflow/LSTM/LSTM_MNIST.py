
# coding: utf-8

# In[2]:


import tensorflow as tf
from tensorflow.contrib import rnn
from tensorflow.examples.tutorials.mnist import input_data


# In[3]:


mnist =  input_data.read_data_sets("/tmp/data/", one_hot=True)


# In[4]:


learning_rate = 0.001
training_steps = 10000
batch_size = 128
display_step = 200


# In[5]:


num_input = 28
timesteps = 28
num_hidden = 128
num_classes = 10

X = tf.placeholder(tf.float32, [None, timesteps, num_input])
Y = tf.placeholder(tf.float32, [None, num_classes])


# In[6]:


weights ={
    'out':tf.Variable(tf.random_normal([num_hidden, num_classes]))
}

biases = {
    'out':tf.Variable(tf.random_normal([num_classes]))
}


# In[7]:


def RNN(x, weights, biases):
    x = tf.unstack(x, axis=1)
    lstm_cell = rnn.BasicLSTMCell(num_hidden, forget_bias=1.0)
    
    outputs, states = rnn.static_rnn(lstm_cell, x, dtype=tf.float32)
    
    return tf.matmul(outputs[-1], weights['out']) +biases['out']


# In[8]:


logits = RNN(X, weights, biases)
prediction = tf.nn.softmax(logits)

loss_op = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(logits=logits, labels=Y))
train_op = tf.train.AdamOptimizer(learning_rate).minimize(loss_op)

correct_pred = tf.equal(tf.argmax(prediction, 1), tf.argmax(Y, 1))
accuracy = tf.reduce_mean(tf.cast(correct_pred, tf.float32))


# In[9]:


sess = tf.Session()
sess.run(tf.global_variables_initializer())

for step in range(training_steps):
    batch_x, batch_y = mnist.train.next_batch(batch_size)
    batch_x = batch_x.reshape((batch_size, timesteps, num_input))
    sess.run(train_op, feed_dict={X:batch_x, Y:batch_y})
    if step%display_step ==0:
        loss, acc = sess.run([loss_op, accuracy], feed_dict={X:batch_x, Y:batch_y})
        print("Step, Minibatch loss, accuracy: {}, {}, {}".format(step, loss, acc))
        
print("Optimization finished")
test_len = 1024
test_x = mnist.test.images[:test_len].reshape((-1, timesteps, num_input))
test_y = mnist.test.labels[:test_len]
loss, acc = sess.run([loss_op, accuracy], feed_dict={X:test_x, Y:test_y})
print("Test loss, accuracy: {}, {}, {}".format(step, loss, acc))
sess.close()


# In[10]:


sess.close()

