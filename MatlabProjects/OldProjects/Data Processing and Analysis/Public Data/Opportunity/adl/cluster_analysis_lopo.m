clear; load('features2');
f = features2;

clusters = [];
alpha_vals = 1:0.25:4;
k_max = 5;

for k = 1:k_max    
    for subj=1:4        
        
        fprintf('k=%d : %d\n', k, subj);
        XTr = f(f(:,1)~=subj & f(:, end)>0, 3:5);
        X = f(f(:,1)==subj & f(:,2)<=5, 3:6);
        XTs = X(:, 1:3);
        labels = X(:, 4);
        labels = (labels>0);
            
        [idx, C] = kmedoids(XTr,k);            
            
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
            
            clusters(k).alpha(alpha_ix).subject(subj).tp = tp;
            clusters(k).alpha(alpha_ix).subject(subj).fn = fn;

            clusters(k).alpha(alpha_ix).subject(subj).covered_count = sum(covered);
            clusters(k).alpha(alpha_ix).subject(subj).total_count = length(covered);
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
            
            tp = tp + clusters(k).alpha(alpha_ix).subject(subj).tp;
            fn = fn + clusters(k).alpha(alpha_ix).subject(subj).fn;
            
            covered_count = covered_count + clusters(k).alpha(alpha_ix).subject(subj).covered_count;
            total_count = total_count + clusters(k).alpha(alpha_ix).subject(subj).total_count;
        end
        
        clusters(k).alpha(alpha_ix).tp = tp;
        clusters(k).alpha(alpha_ix).fn = fn;

        clusters(k).alpha(alpha_ix).covered_count = covered_count;
        clusters(k).alpha(alpha_ix).total_count = total_count;
            
        clusters(k).alpha(alpha_ix).fnr = fn/(tp+fn);
        clusters(k).alpha(alpha_ix).drate = covered_count/total_count;
        
    end
end

clusters_lopo = clusters;
save('clusters_lopo','clusters_lopo');
return

thresholds = 0:0.1:1;
fnr = zeros(length(thresholds), 1);

figure
plot(thresholds, fnr);
ylim([0,1]);
hold on
plot(thresholds, data_ratio);

segments_lopo = segments;
save('segments_lopo','segments_lopo');