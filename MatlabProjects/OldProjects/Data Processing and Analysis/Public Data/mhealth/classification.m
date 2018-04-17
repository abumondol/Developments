data = mhealth_features;
pos = 1;
cols = [1:15, 46];
window_durations = [1, 2, 3, 6];
num_trees = 100;
results = [];

y = data(:,end-3);
y(y<=3) = 3;
y = y-2;
%data(:,end-3) = data(:, end-3) + 1;
data(:,end-3) = y;
for w =4%1:length(window_durations)
    f = data(data(:,end-2)==window_durations(w), :);
    size(f)
    
    conf_mat = 0;
    for sub = 1:10    
        fprintf('***************window size: %d, subject: %d*****************\n',window_durations(w), sub); 
        train_data = f(f(:,end)~=sub , cols);
        test_data = f(f(:,end)==sub, cols);    
    
        X = train_data(:,1:end-1);
        Y = train_data(:, end);        
        %model = TreeBagger(num_trees, X,Y, 'NumPredictorsToSample', 'all');
        model = TreeBagger(num_trees, X,Y);
        
        X = test_data(:,1:end-1);
        Y = test_data(:, end);        
        c = classification_conf_mat(model, X, Y, 1:10);        
        conf_mat = conf_mat + c;        
        r = classification_accuracy_scores(c)
        results(w).subject(sub).res = r;
        results(w).subject(sub).conf_matrix = c;
    end
    
    fprintf('***************window size: %d, subject: all*****************\n',window_durations(w)); 
    results(w).res = classification_accuracy_scores(conf_mat)
    results(w).conf_mat = classification_accuracy_scores(conf_mat);
end
