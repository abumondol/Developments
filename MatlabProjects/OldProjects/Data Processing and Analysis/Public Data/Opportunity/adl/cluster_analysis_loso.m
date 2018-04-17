clear; load('features2');
f = features2;

clusters = [];
alpha_vals = 1:0.25:4;
k_max = 5;

for k = 1:k_max    
    for subj=1:4        
        for sess = 1:5
            fprintf('k=%d : %d, %d\n', k, subj, sess);
            XTr = f(f(:,1)==subj & f(:,2)~=sess & f(:, end)>0, 3:5);
            X = f(f(:,1)==subj & f(:,2)==sess, 3:6);
            XTs = X(:, 1:3);
            labels = X(:, 4);
            labels = f(f(:,1)==subj & f(:,2)==sess, end);
            labels = (labels>0);
            
            [idx, C] = kmedoids(XTr,k);            
            %clusters(k).subject(subj).session(sess).C = C;
            %clusters(k).subject(subj).session(sess).idx = C;
            
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
        end
    end
end

for k = 1:k_max
    alpha_count = length(clusters(k).alpha);
    for alpha_ix = 1:length(alpha_vals)
        tp = 0;
        fn= 0;
        covered_count= 0;
        total_count= 0;
            
        for subj=1:4
            tp_sub = 0;
            fn_sub = 0;
            covered_count_sub = 0;
            total_count_sub = 0;
            for sess = 1:5
                
                tp_sub = tp_sub + clusters(k).alpha(alpha_ix).subject(subj).session(sess).tp;
                fn_sub  = fn_sub + clusters(k).alpha(alpha_ix).subject(subj).session(sess).fn;
                
                covered_count_sub = covered_count_sub + clusters(k).alpha(alpha_ix).subject(subj).session(sess).covered_count;
                total_count_sub = total_count_sub + clusters(k).alpha(alpha_ix).subject(subj).session(sess).total_count;
                
            end
            
            clusters(k).alpha(alpha_ix).subject(subj).tp = tp_sub;
            clusters(k).alpha(alpha_ix).subject(subj).fn = fn_sub;
                
            clusters(k).alpha(alpha_ix).subject(subj).covered_count = covered_count_sub;
            clusters(k).alpha(alpha_ix).subject(subj).total_count = total_count_sub;
            
            tp = tp + clusters(k).alpha(alpha_ix).subject(subj).session(sess).tp;
            fn = fn + clusters(k).alpha(alpha_ix).subject(subj).session(sess).fn;
            
            covered_count = covered_count + clusters(k).alpha(alpha_ix).subject(subj).session(sess).covered_count;
            total_count = total_count + clusters(k).alpha(alpha_ix).subject(subj).session(sess).total_count;
        end
        
        clusters(k).alpha(alpha_ix).tp = tp;
        clusters(k).alpha(alpha_ix).fn = fn;

        clusters(k).alpha(alpha_ix).covered_count = covered_count;
        clusters(k).alpha(alpha_ix).total_count = total_count;
            
        clusters(k).alpha(alpha_ix).fnr = fn/(tp+fn);
        clusters(k).alpha(alpha_ix).drate = covered_count/total_count;
        
    end
end

clusters_loso = clusters;
save('clusters_loso','clusters_loso');
return

thresholds = 0:0.1:1;
fnr = zeros(length(thresholds), 1);

figure
plot(thresholds, fnr);
ylim([0,1]);
hold on
plot(thresholds, data_ratio);

segments_loso = segments;
save('segments_loso','segments_loso');