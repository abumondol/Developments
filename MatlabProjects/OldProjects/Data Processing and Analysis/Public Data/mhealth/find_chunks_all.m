sub_count = length(mhealth_data)
mhealth_chunks = [];


for sub = 1:sub_count
    sess_count = length(mhealth_data(sub).session);
    for sess = 1:sess_count
        fprintf('sub:%d, sess:%d\n', sub, sess);
        labels = mhealth_data(sub).session(sess).labels;                
        mhealth_chunks(sub).session(sess).chunks = find_chunks(labels); 
    end
end

save('mhealth_chunks', 'mhealth_chunks');
