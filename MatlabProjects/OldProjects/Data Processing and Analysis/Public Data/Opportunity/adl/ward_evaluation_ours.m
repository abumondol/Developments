clear; load('../oppdata'); load('features');

window_size = 30;
step_size = 15;

NumTrees = 50;

alpha_vals = 1:0.25:4;
my_res = cell{k, length(alpha_vals)};
my_ward = cell{k, length(alpha_vals)};

for k=1:5
    for aix=1:length(alpha_vals)
        alpha = alpha_vals(aix);
        
        t_all=[];
        ground_all=[];
        eval_all = [];
        
        allres = cell(4, 5);
        for subj=1:4
            for sess=1:5        
                [train, test] = get_train_test(subj, sess, features);
                gTrain = train(:,1:3);
                XTrain = train(:, 4:end-1);
                YTrain = train(:, end);                    
        
                fprintf('%d %d -- %d, %d\n', k, aix, subj, sess);
                gTest = features{subj, sess}.xy(:, 1;3);
                XTest = features{subj, sess}.xy(:, 4:end-1);
                YTest = features{subj, sess}.xy(:, end);
                
                [idx, C] = kmedoids(XTrain,k);
                
                mu = zeros(k, 1);
                for j = 1:k
                    members = XTr(idx==j, :);
                    centroid = C(j,:)';                
                    theta = acos(members*centroid);
                    mu(k) = mean(theta);
                end
                
                for alpha_ix = 1:length(alpha_vals) 
                alpha = alpha_vals(alpha_ix);
                covered = zeros(length(labels), 1);            
                for j=1:k
                    max_theta = alpha*mu(k);
                    min_costheta = cos(max_theta);
                    centroid = C(j,:)';                
                    costheta = XTs*centroid;
                    covered = covered + (costheta >= min_costheta);
                end
                
                covered = (covered>0);
            
                tp = sum(covered.*labels);            
                fn = sum(labels) - tp;
                fnr_sess = fn/(tp+fn);
                drate_sess = sum(covered)/length(covered);

                clusters(k).alpha(alpha_ix).subject(subj).session(sess).tp = tp;
                clusters(k).alpha(alpha_ix).subject(subj).session(sess).fn = fn;
                
                clusters(k).alpha(alpha_ix).subject(subj).session(sess).covered_count = sum(covered);
                clusters(k).alpha(alpha_ix).subject(subj).session(sess).total_count = length(covered);
            end   
                
                
                
                B = TreeBagger(NumTrees, XTrain, YTrain);
                
                
                
                
                
                
        
                YPred = predict(B, XTest);
                YPred = cell2mat(YPred);            
                YPred = str2num(YPred);
        
                t = oppdata{subj, sess}.t;
                if isempty(t_all)
                    t_all = t;
                else
                    t = t +  t_all(end) + t(2)-t(1);
                    t_all = [t_all; t];
                end
        
                eval = benchmark_expandingLabels(YPred, window_size, length(t), step_size);
                eval_all = [eval_all; eval];

                ground = oppdata{subj, sess}.labels(:,5);
                ground = rename_labels(ground);        
                ground_all= [ground_all;ground];
                
                res.YTest = YTest;
                res.YPred = YPred;
                allres(subj, sess) = res;
            end
        end

        class_count = length(unique(eval_all));

        t = t_all;
        t2 = [t(2:end, :); t(end)+t(2)-t(1)];
        ground = [t, t2, ground_all];
        eval = [t, t2, eval_all];        

        [ward_t, ward_s, ward_e, ward_meanLenOU] = benchmark_mset(eval, ground, class_count);

        ward_res = benchmark_ward_errors({ward_t});
        
        my_res{k,aix} = allres;
        my_ward{k, aix} = ward_res{1};
    end
end

