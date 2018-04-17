clear; load('../oppdata'); load('features');

window_size = 30;
step_size = 15;

NumTrees = 50;
alpha_vals = 1:0.25:4;

our_res = cell(4,1);
for k=1:5           
        
    allres = cell(4, 5);
    for subj=1:4
        for sess=1:5        
            [train, test] = get_train_test(subj, sess, features);
            gTrain = train(:,1:3);
            XTrain = train(:, 4:end-1);
            YTrain = train(:, end);                    

            fprintf('%d -- %d, %d\n', k, subj, sess);
            gTest = features{subj, sess}.xy(:, 1:3);
            XTest = features{subj, sess}.xy(:, 4:end-1);
            YTest = features{subj, sess}.xy(:, end);

            [idx, C] = kmedoids(gTrain,k);

            mu = zeros(k, 1);
            for j = 1:k
                members = gTrain(idx==j, :);
                centroid = C(j,:)';                
                theta = acos(members*centroid);
                mu(k) = mean(theta);
            end
            
            alpha_res = cell(length(alpha_vals),1);
            for alpha_ix = 1:length(alpha_vals)
                res = [];
                alpha = alpha_vals(alpha_ix);
                covered = zeros(length(gTest), 1);
                covered_tr = zeros(length(YTrain), 1);
                for j=1:k
                    max_theta = alpha*mu(k);
                    min_costheta = cos(max_theta);
                    centroid = C(j,:)';                
                    costheta = gTest*centroid;
                    covered = covered + (costheta >= min_costheta);
                    
                    costheta = gTrain*centroid;
                    covered_tr = covered_tr + (costheta >= min_costheta);
                end
                
                XTrain2 = XTrain(covered_tr > 0, :);
                YTrain2 = YTrain(covered_tr > 0, :);
                B = TreeBagger(NumTrees, XTrain2, YTrain2);
                YPred = predict(B, XTest);
                YPred = cell2mat(YPred);            
                YPred = str2num(YPred);

                res.covered = (covered>0);
                res.YPred = YPred;
                alpha_res{alpha_ix,1} = res;
            end
            
            allres{subj, sess} = alpha_res;            
        end  
    end
    
    our_res{k} = allres;
end

save('our_res','our_res');
