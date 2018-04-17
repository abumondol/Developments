sub_count = 52; %length(data);

for s =1:sub_count
    fprintf(' Decision tree Subject %d\n', s);  
    X_train = [];
    Y_train = [];
    
    X_test = features(s).X;    
    
    for i=1:sub_count
        if i~=s
            X_train =[X_train; features(s).X];
            Y_train = [Y_train; features(s).Y];
        end
    end
    
    mdl = fitctree(X_train, Y_train);    
    [label,score,node,cnum] = predict(mdl, X_test);
    
    features(s).Y_pred = label;
    features(s).scores = score;    
end