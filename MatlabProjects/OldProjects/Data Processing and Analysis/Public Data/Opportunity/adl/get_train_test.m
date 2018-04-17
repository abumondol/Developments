function [train, test] = get_train_test(subject, session, features)
    
    train = [];
    test = [];
    if session == 0 % LOPO        
        for subj=1:4
            for sess=1:6
                if subj ~= subject
                    train = [train; features{subj, sess}.xy];                    
                elseif session <=5
                    test = [test; features{subj, sess}.xy];
                end
            end
        end         
        
    else        
        test = features{subject, session}.xy;
        for sess=1:6
            if sess ~= session                    
                train = [train; features{subject, sess}.xy];                    
            end
        end        
        
    end

end