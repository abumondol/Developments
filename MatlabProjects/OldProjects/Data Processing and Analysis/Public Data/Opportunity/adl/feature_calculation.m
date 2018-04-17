clear; load('../oppdata');

features = cell(4, 6);        
window_size = 30;
step_size = 15;

for subj = 1:4
    for sess = 1:6
        fprintf('Features: %d, %d\n', subj, sess);        
        features{subj, sess} = features_one_session(subj, sess, oppdata, window_size, step_size);
    end
end
    
save('features', 'features');