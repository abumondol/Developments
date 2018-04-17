
mg = get_mid_gravity_values(data, indices);
segments = [];

th = 1:0.5:10;
fnr = zeros(length(th), 1);
data_ratio = zeros(length(th), 1);
for th_ix = 1:length(th)
    test_count_total = 0;
    select_count_total = 0;
    
    test_sample_count_total = 0;
    select_sample_count_total = 0;
    
    k = th(th_ix);
    for subj=1:4
        test_count_subj = 0;
        select_count_subj = 0;
        
        test_sample_count_subj = 0;
        select_sample_count_subj = 0;
        
        train = mg(mg(:,1)~=subj, 3:5);            
        [mu, sigma] = normfit(train);
        
        
        for sess = 1:5            
            g = data(subj).session(sess).grav;
            test_sample_count = size(g,1);            
        
            segs = find_segments(g, mu, sigma, k);
            
            test_ix = indices(indices(:,1)==subj & indices(:,2)==sess, 5);
            test_count = length(test_ix);
            isSelected = zeros(test_count,1);
            
            if ~isempty(segs)
                select_sample_count = sum(segs(:,2)-segs(:,1)+1);
                for i = 1:test_count
                    isSelected(i,1) = sum( test_ix(i)>=segs(:,1) & test_ix(i)<=segs(:,2) ); 
                    if  isSelected(i,1)>1
                        fprintf('xxxx problem xxxx');
                        exit(0);
                    end
                end
            
            else
                select_sample_count = 0;
                fprintf('%.2f, %d, %d, %d\n', th_ix, subj, sess, size(segs, 1));            
            end
            
            segments(th_ix).subject(subj).session(sess).th = k;
            segments(th_ix).subject(subj).session(sess).segs = segs;            
            %segments(th_ix).subject(subj).session(sess).max_length = max(segs(:,2)-segs(:,1)+1);
            
            select_count = sum(isSelected);
            segments(th_ix).subject(subj).session(sess).select_count = select_count;
            segments(th_ix).subject(subj).session(sess).test_count = test_count;
            segments(th_ix).subject(subj).session(sess).fnr = (test_count - select_count) / test_count;
            
            segments(th_ix).subject(subj).session(sess).select_sample_count = select_sample_count;
            segments(th_ix).subject(subj).session(sess).test_sample_count = test_sample_count;         
            segments(th_ix).subject(subj).session(sess).data_ratio = select_sample_count / test_sample_count;
            
            select_count_subj = select_count_subj + select_count;
            test_count_subj = test_count_subj + test_count;
            
            select_sample_count_subj = select_sample_count_subj + select_sample_count;
            test_sample_count_subj = test_sample_count_subj + test_sample_count;
            
        end
                
        segments(th_ix).subject(subj).select_count = select_count_subj;
        segments(th_ix).subject(subj).test_count = test_count_subj;
        segments(th_ix).subject(subj).fnr = (test_count_subj - select_count_subj) / test_count_subj;
        
        segments(th_ix).subject(subj).select_sample_count = select_sample_count_subj;
        segments(th_ix).subject(subj).test_sample_count = test_sample_count_subj;
        segments(th_ix).subject(subj).data_ratio = select_sample_count_subj / test_sample_count_subj;
        
        select_count_total = select_count_total + select_count_subj;
        test_count_total = test_count_total + test_count_subj;
        
        select_sample_count_total = select_sample_count_total + select_sample_count_subj;
        test_sample_count_total = test_sample_count_total + test_sample_count_subj;
    end
    
    segments(th_ix).select_count = select_count_total;
    segments(th_ix).test_count = test_count_total;
    segments(th_ix).fnr = (test_count_total - select_count_total) / test_count_total;
    
    segments(th_ix).select_sample_count = select_sample_count_total;
    segments(th_ix).test_sample_count = test_sample_count_total;
    segments(th_ix).data_ratio = select_sample_count_total / test_sample_count_total;
    
    
    fnr(th_ix,1) = segments(th_ix).fnr;
    data_ratio(th_ix,1) = segments(th_ix).data_ratio;
end


figure
plot(th, fnr);
ylim([0,1]);
hold on
plot(th, data_ratio);

segments_lopo = segments;
save('segments_lopo','segments_lopo');