%load('indices');
%load('bres');
%load('baseline');

radius = 90;
bstat1 = [];
d = baseline.features;
    
for NumTrees = 50:50:250
    fprintf('RF: %d, \n', NumTrees);
    nt = NumTrees/50;    
    
    tp_fp_fn_total = zeros(2, 3);
    for subj = 1:4
        f = d(d(:, 1) == subj, :);
    
        tp_fp_fn_subj = zeros(2, 3);        
        for sess = 1:5               
            ix_gt = indices(indices(:,1)==subj & indices(:,2)==sess, 5:6);           
            gtix_bite = ix_gt(ix_gt(:,2)==1, 1);
            gtix_sip = ix_gt(ix_gt(:,2)==2, 1);            
        
            WindowIndices = f(f(:,2)==sess, 3); %mid indices of all windows of the session        
        
            y = bres1(subj).session(sess).ntree(nt).y;
            y = cell2mat(y);            
            y = str2num(y);                        
            ix_res = [WindowIndices(y>0, 1), y(y>0, 1)];            
            
            resix_bite = ix_res(ix_res(:,2)==1, 1);
            resix_sip = ix_res(ix_res(:,2)==2, 1);
            
            
            tp_fp_fn = zeros(2, 3);
            tp_fp_fn(1, :) = find_tp_fp_fn(gtix_bite, resix_bite, radius);
            tp_fp_fn(2, :) = find_tp_fp_fn(gtix_sip, resix_sip, radius);            
            bstat1(nt).sub(subj).sessions(sess).tp_fp_fn = tp_fp_fn;            
            tp_fp_fn_subj = tp_fp_fn_subj + tp_fp_fn;
        end       
        
        bstat1(nt).sub(subj).tp_fp_fn = tp_fp_fn_subj;                
        pr_rc_f1 = zeros(2, 3);        
        for j=1:2
            pr_rc_f1(j, 1) = tp_fp_fn_subj(j,1)/(tp_fp_fn_subj(j,1)+tp_fp_fn_subj(j,2));
            pr_rc_f1(j, 2) = tp_fp_fn_subj(j,1)/(tp_fp_fn_subj(j,1)+tp_fp_fn_subj(j,3));            
        end
        pr = pr_rc_f1(:,1);
        rc = pr_rc_f1(:,2);
        pr_rc_f1(:, 3) = 2*pr.*rc./(pr+rc);            
        
        bstat1(nt).sub(subj).pr_rc_f1 = pr_rc_f1;
        tp_fp_fn_total = tp_fp_fn_total + tp_fp_fn_subj;        
    end
    
    bstat1(nt).tp_fp_fn = tp_fp_fn_total;                
    pr_rc_f1 = zeros(2, 3);        
    for j=1:2
        pr_rc_f1(j, 1) = tp_fp_fn_total(j,1)/(tp_fp_fn_total(j,1)+tp_fp_fn_total(j,2));
        pr_rc_f1(j, 2) = tp_fp_fn_total(j,1)/(tp_fp_fn_total(j,1)+tp_fp_fn_total(j,3));        
    end
    
    pr = pr_rc_f1(:,1);
    rc = pr_rc_f1(:,2);
    pr_rc_f1(:, 3) = 2*pr.*rc./(pr+rc);

    bstat1(nt).pr_rc_f1 = pr_rc_f1;
    
end

save('bstat1', 'bstat1');