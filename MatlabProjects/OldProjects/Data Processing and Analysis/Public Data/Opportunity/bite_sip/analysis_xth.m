clear; load('..\oppdata');load('indices');
mg = get_mid_gravity_values(oppdata, indices);


xmins = 0:0.05:1;
len = length(xmins);
bitecount = zeros(len, 2);  %total, selected
samplecount = zeros(len, 2); %total ,selected
extracount = zeros(len, 1);
for xix = 1:length(xmins)
    for subj=1:4
        for sess = 1:5            
            g = oppdata{subj, sess}.grav;
            samplecount(xix, 1) = samplecount(xix, 1) + size(g,1);
            segs = our_find_segments_xth(g, xmins(xix));

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

                extracount(xix) = extracount(xix) + 6*30*size(segs,1);
            else
                select_sample_count = 0;
                fprintf('Empty segs: %.2f, %d, %d, %d\n', xmins(xix), subj, sess, size(segs, 1));            
            end
            
            samplecount(xix, 2) = samplecount(xix, 2) + select_sample_count;                       
            
            select_count = sum(isSelected);            
            bitecount(xix, 1) = bitecount(xix, 1) + test_count;
            bitecount(xix, 2) = bitecount(xix, 2) + select_count;            
        end                
        
    end    
    
end
%samplecount(:,2) = samplecount(:,2) + extracount(:,1);

fnr = (bitecount(:,1) - bitecount(:,2))./bitecount(:,1);
dratio = samplecount(:,2)./samplecount(:,1);

figure
plot(xmins, fnr);
ylim([0,1]);
hold on
plot(xmins, dratio);
lgd =  legend('FNR_1', 'Potential segment duration to DS ratio');
lgd.FontSize = 12;
xlabel('g_{xmin}', 'FontSize', 14);
ylabel('Percentage(%)', 'FontSize', 14);
